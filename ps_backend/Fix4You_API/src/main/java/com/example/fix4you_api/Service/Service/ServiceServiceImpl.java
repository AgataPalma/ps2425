package com.example.fix4you_api.Service.Service;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Enums.ServiceStateEnum;
import com.example.fix4you_api.Data.Models.Client;
import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.MongoRepositories.ServiceRepository;
import com.example.fix4you_api.Service.Client.ClientService;
import com.example.fix4you_api.Service.Professional.ProfessionalService;
import com.example.fix4you_api.Service.ScheduleAppointment.ScheduleAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;

    private final ClientService clientService;
    private final ProfessionalService professionalService;
    private final ScheduleAppointmentService scheduleAppointmentService;

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

}
