package com.example.fix4you_api.Service.Service;

import com.example.fix4you_api.Data.Enums.ServiceStateEnum;
import com.example.fix4you_api.Data.Models.CategoryDescription;
import com.example.fix4you_api.Data.Models.Client;
import com.example.fix4you_api.Data.Models.ClientTotalSpent;
import com.example.fix4you_api.Data.Models.Dtos.ServiceDashboardDTO;
import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.Models.*;
import com.example.fix4you_api.Data.MongoRepositories.ServiceRepository;
import com.example.fix4you_api.Rsql.RsqlQueryService;
import com.example.fix4you_api.Service.CategoryDescription.CategoryDescriptionService;
import com.example.fix4you_api.Service.Client.ClientService;
import com.example.fix4you_api.Service.Professional.ProfessionalService;
import com.example.fix4you_api.Service.ScheduleAppointment.ScheduleAppointmentService;
import com.example.fix4you_api.Service.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final ClientService clientService;
    private final UserService userService;
    private final ProfessionalService professionalService;
    private final ScheduleAppointmentService scheduleAppointmentService;
    private final CategoryDescriptionService categoryDescriptionService;
    private final RsqlQueryService rsqlQueryService;

    @Override
    public com.example.fix4you_api.Data.Models.Service getById(String id) {
        return findOrThrow(id);
    }

    @Override
    public com.example.fix4you_api.Data.Models.Service createService(com.example.fix4you_api.Data.Models.Service service) {

        if(service.getProfessionalId() != null) {
            List<CategoryDescription> categoryDescriptions = categoryDescriptionService.getCategoriesDescriptionByProfessionalIdAndCategoryId(service.getProfessionalId(), service.getCategory().getId());

            if (categoryDescriptions.size() > 1) {
                throw new IllegalStateException(
                        "Existe mais de uma descrição de categoria para o profissional "
                                + service.getProfessionalId()
                                + " na categoria "
                                + service.getCategory().getName()
                );
            }

            if (categoryDescriptions.isEmpty()) {
                throw new IllegalStateException(
                        "Nenhuma descrição de categoria encontrada para o profissional "
                                + service.getProfessionalId()
                                + " na categoria "
                                + service.getCategory().getName()
                );
            }

            CategoryDescription professionalCategoryDescription = categoryDescriptions.get(0);
            service.setPrice(professionalCategoryDescription.getMediumPricePerService());
        }

        return serviceRepository.save(service);
    }

    @Override
    public List<ServiceDashboardDTO> getServices(String filter, String sort) {
        List<com.example.fix4you_api.Data.Models.Service> list = new ArrayList<>();
        if (isEmpty(filter) && isEmpty(sort)) {
            list =  serviceRepository.findAll();
        }
        list = rsqlQueryService.findAll(com.example.fix4you_api.Data.Models.Service.class, filter, sort);

        List<ServiceDashboardDTO> listDashboard = new ArrayList<>();
        for (com.example.fix4you_api.Data.Models.Service service : list) {
            ServiceDashboardDTO dto = new ServiceDashboardDTO(service);
            Client client = clientService.getClientByIdNotThrow(service.getClientId());
            if(client == null) continue;
            dto.setClientProfileImage(client.getProfileImage());

            listDashboard.add(dto);
        }

        return listDashboard;
    }

    @Override
    public List<ClientServiceCount> getTopActivitiesClients() {
        List<com.example.fix4you_api.Data.Models.Service> services = serviceRepository.findTop10ClientsWithMostServices();

        // Group by clientId and count services
        Map<String, Long> clientServiceCounts = services.stream()
                .filter(service -> service.getClientId() != null)
                .filter(service -> service.getProfessionalId() != null)
                .filter(service -> service.getState() == ServiceStateEnum.COMPLETED)
                .collect(Collectors.groupingBy(com.example.fix4you_api.Data.Models.Service::getClientId, Collectors.counting()));

        // Sort and limit to top 10
        List<ClientServiceCount> listClientServiceCount = clientServiceCounts.entrySet().stream()
                .filter(service -> service.getKey() != null)
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(entry -> new ClientServiceCount(
                        entry.getKey(),
                        null,
                        entry.getValue())
                )
                .collect(Collectors.toList());

        for (var i=0; i<listClientServiceCount.size(); i++){
            try{
                ClientServiceCount clientServiceCount = listClientServiceCount.get(i);
                clientServiceCount.setClientName(clientService.getClientById(clientServiceCount.getClientId()).getName());
            } catch(Exception e){

            }
        }

        return listClientServiceCount;
    }

    @Override
    public List<ClientServiceCount> sendEmailTopActivitiesClients() {
        List<ClientServiceCount> listClientServiceCount = this.getTopActivitiesClients();

        for(var i=0; i< listClientServiceCount.size(); i++){
            if(listClientServiceCount.get(i).getClientId() != null) {
                User user = userService.getUser(listClientServiceCount.get(i).getClientId());
                if(user != null) {
                    userService.sendEmailTopUsers(user);
                }
            }
        }

        return listClientServiceCount;
    }

    @Override
    public List<ProfessionalServiceCount> getTopActivitiesProfessionals() {
        List<com.example.fix4you_api.Data.Models.Service> services = serviceRepository.findTop10ProfessionalsWithMostServices();

        // Group by clientId and count services
        Map<String, Long> professionalServiceCounts = services.stream()
                .filter(service -> service.getProfessionalId() != null)
                .filter(service -> service.getClientId() != null)
                .filter(service -> service.getState() == ServiceStateEnum.COMPLETED)
                .collect(Collectors.groupingBy(com.example.fix4you_api.Data.Models.Service::getProfessionalId, Collectors.counting()));

        // Sort and limit to top 10
        List<ProfessionalServiceCount> listProfessionalServiceCount = professionalServiceCounts.entrySet().stream()
                .filter(service -> service.getKey() != null)
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(entry -> new ProfessionalServiceCount(
                        entry.getKey(),
                        null,
                        entry.getValue())
                )
                .collect(Collectors.toList());

        for (var i=0; i<listProfessionalServiceCount.size(); i++){
            try{
                ProfessionalServiceCount professionalServiceCount = listProfessionalServiceCount.get(i);
                professionalServiceCount.setProfessionalName(professionalService.getProfessionalById(professionalServiceCount.getProfessionalId()).getName());
            } catch(Exception e){

            }
        }

        return listProfessionalServiceCount;
    }

    @Override
    public List<ProfessionalServiceCount> sendEmailTopActivitiesProfessionals() {
        List<ProfessionalServiceCount> listProfessionalServiceCount = this.getTopActivitiesProfessionals();

        for(var i=0; i< listProfessionalServiceCount.size(); i++){
            if(listProfessionalServiceCount.get(i).getProfessionalId() != null) {
                User user = userService.getUser(listProfessionalServiceCount.get(i).getProfessionalId());
                if(user != null) {
                    userService.sendEmailTopUsers(user);
                }
            }
        }

        return listProfessionalServiceCount;
    }

    @Override
    public List<ClientTotalSpent> getTopPriceClients() {
        // Fetch the raw results: clientId and totalSpent
        List<com.example.fix4you_api.Data.Models.Service> results = serviceRepository.findTopClientsByTotalSpending();

        // Group by clientId and count services
        Map<String, Double> clientServiceCounts = results.stream()
                .filter(service -> service.getClientId() != null)
                .filter(service -> service.getProfessionalId() != null)
                .filter(service -> service.getState() == ServiceStateEnum.COMPLETED)
                .collect(Collectors.groupingBy(com.example.fix4you_api.Data.Models.Service::getClientId, Collectors.summingDouble(com.example.fix4you_api.Data.Models.Service::getPrice)));

        // Process the results
        List<ClientTotalSpent> listClientTotalSpent = clientServiceCounts.entrySet().stream()
                .map(result -> new ClientTotalSpent(
                        (String) result.getKey(),
                        null,
                        ((Number) result.getValue()).doubleValue()     // totalSpent
                ))
                .sorted((a, b) -> Double.compare(b.getTotalSpent(), a.getTotalSpent())) // Sort by totalSpent (descending)
                .limit(10) // Top 10 clients
                .collect(Collectors.toList());

        for (var i=0; i<listClientTotalSpent.size(); i++){
            try{
                ClientTotalSpent clientTotalSpent = listClientTotalSpent.get(i);
                clientTotalSpent.setClientName(clientService.getClientById(clientTotalSpent.getClientId()).getName());
            } catch(Exception e){

            }
        }

        return listClientTotalSpent;
    }

    @Override
    public List<ClientTotalSpent> sendEmailTopPriceClients() {
        List<ClientTotalSpent> listClientTotalSpent = this.getTopPriceClients();

        for(var i=0; i< listClientTotalSpent.size(); i++){
            if(listClientTotalSpent.get(i).getClientId() != null) {
                User user = userService.getUser(listClientTotalSpent.get(i).getClientId());
                if(user != null) {
                    userService.sendEmailTopUsers(user);
                }
            }
        }

        return listClientTotalSpent;
    }

    @Transactional
    @Override
    public com.example.fix4you_api.Data.Models.Service updateService(String id, com.example.fix4you_api.Data.Models.Service service) {
        com.example.fix4you_api.Data.Models.Service serviceToUpdate = findOrThrow(id);
        BeanUtils.copyProperties(service, serviceToUpdate);
        return serviceRepository.save(serviceToUpdate);
    }

    @Transactional
    @Override
    public void deleteService(String id) {
        com.example.fix4you_api.Data.Models.Service service = findOrThrow(id);
        serviceRepository.delete(service);
    }

    @Override
    public List<com.example.fix4you_api.Data.Models.Service> getServicesByProfessionalId(String professionalId) {
        return this.serviceRepository.findByProfessionalId(professionalId);
    }

    @Override
    public List<com.example.fix4you_api.Data.Models.Service> getServicesByClientId(String clientId) {
        return this.serviceRepository.findByClientId(clientId);
    }

    @Override
    public List<com.example.fix4you_api.Data.Models.Service> getServicesByCategoryId(String categoryId) {
        return this.serviceRepository.findByCategory_Id(categoryId);
    }

    @Override
    public List<com.example.fix4you_api.Data.Models.Service> getServicesByUrgency() {
        return this.serviceRepository.findByUrgentTrueAndState(ServiceStateEnum.PENDING);
    }

    @Override
    public List<com.example.fix4you_api.Data.Models.Service> getServicesByProfessionalIdAndState(String professionalId, ServiceStateEnum state) {
        return this.serviceRepository.findByProfessionalIdAndState(professionalId, state);
    }

    @Override
    @Transactional
    public void deleteServicesForClient(String clientId) {
        List<com.example.fix4you_api.Data.Models.Service> services = getServicesByClientId(clientId);

        for(com.example.fix4you_api.Data.Models.Service service: services) {
            Professional professional = null;

            if (service.getProfessionalId() != null) {
                try {
                    professional = professionalService.getProfessionalById(service.getProfessionalId());
                } catch (NoSuchElementException e) {
                    // Não encontrou o profissional, mas continua a execução
                }
            }

            if(professional == null) {
                scheduleAppointmentService.deleteScheduleAppointment(service.getId());
                serviceRepository.deleteById(service.getId());
            }
        }
    }

    @Override
    @Transactional
    public void deleteServicesForProfessional(String professionalId) {
        List<com.example.fix4you_api.Data.Models.Service> services = getServicesByProfessionalId(professionalId);

        for(com.example.fix4you_api.Data.Models.Service service: services) {
            Client client = null;

            if (service.getClientId() != null) {
                try {
                    client = clientService.getClientById(service.getClientId());
                } catch (NoSuchElementException e) {
                    // Não encontrou o profissional, mas continua a execução
                }
            }

            if(client == null) {
                scheduleAppointmentService.deleteScheduleAppointment(service.getId());
                serviceRepository.deleteById(service.getId());
            }
        }
    }

    @Override
    public boolean checkServicesToDeleteClient(String clientId) {
        List<com.example.fix4you_api.Data.Models.Service> services = getServicesByClientId(clientId);

        for(com.example.fix4you_api.Data.Models.Service service: services) {
            if(service.getState() != ServiceStateEnum.COMPLETED && service.getState() != ServiceStateEnum.CANCELED) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean checkServicesToDeleteProfessional(String professionalId) {
        List<com.example.fix4you_api.Data.Models.Service> services = getServicesByProfessionalId(professionalId);

        for(com.example.fix4you_api.Data.Models.Service service: services) {
            if(service.getState() != ServiceStateEnum.COMPLETED && service.getState() != ServiceStateEnum.CANCELED) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean serviceExists(String serviceId) {
        return this.serviceRepository.existsById(serviceId);
    }

    private com.example.fix4you_api.Data.Models.Service findOrThrow(String id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Serviço %s não encontrado!", id)));
    }

}