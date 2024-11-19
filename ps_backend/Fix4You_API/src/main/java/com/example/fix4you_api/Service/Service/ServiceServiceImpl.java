package com.example.fix4you_api.Service.Service;

import com.example.fix4you_api.Data.Enums.ServiceStateEnum;
import com.example.fix4you_api.Data.Models.Client;
import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.MongoRepositories.ServiceRepository;
import com.example.fix4you_api.Rsql.RsqlQueryService;
import com.example.fix4you_api.Service.Client.ClientService;
import com.example.fix4you_api.Service.Professional.ProfessionalService;
import com.example.fix4you_api.Service.ScheduleAppointment.ScheduleAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final ClientService clientService;
    private final ProfessionalService professionalService;
    private final ScheduleAppointmentService scheduleAppointmentService;
    private final RsqlQueryService rsqlQueryService;

    @Override
    public com.example.fix4you_api.Data.Models.Service getById(String id) {
        return findOrThrow(id);
    }

    @Override
    public com.example.fix4you_api.Data.Models.Service createService(com.example.fix4you_api.Data.Models.Service service) {
        return serviceRepository.save(service);
    }

    @Override
    public List<com.example.fix4you_api.Data.Models.Service> getServices(String filter, String sort) {
        if (isEmpty(filter) && isEmpty(sort)) {
            return serviceRepository.findAll();
        }
        return rsqlQueryService.findAll(com.example.fix4you_api.Data.Models.Service.class, filter, sort);
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
    public List<com.example.fix4you_api.Data.Models.Service> getServicesByProfessionalIdAndState(String professionalId, ServiceStateEnum state) {
        return this.serviceRepository.findByProfessionalIdAndState(professionalId, state);
    }

    @Override
    @Transactional
    public void deleteServicesForClient(String clientId) {
        List<com.example.fix4you_api.Data.Models.Service> services = getServicesByClientId(clientId);

        for(com.example.fix4you_api.Data.Models.Service service: services) {
            Professional professional = professionalService.getProfessionalById(service.getProfessionalId());

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
            Client client = clientService.getClientById(service.getClientId());

            if(client == null) {
                scheduleAppointmentService.deleteScheduleAppointment(service.getId());
                serviceRepository.deleteById(service.getId());
            }
        }
    }

    @Override
    public boolean serviceExists(String serviceId) {
        return this.serviceRepository.existsById(serviceId);
    }

    private com.example.fix4you_api.Data.Models.Service findOrThrow(String id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Service %s not found", id)));
    }

}
