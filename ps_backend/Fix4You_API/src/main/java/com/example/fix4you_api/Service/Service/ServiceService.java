package com.example.fix4you_api.Service.Service;

import com.example.fix4you_api.Data.Enums.ServiceStateEnum;
import com.example.fix4you_api.Data.Models.Service;

import java.util.List;

public interface ServiceService {
    List<Service> getServicesByProfessionalId(String professionalId);
    List<Service> getServicesByClientId(String clientId);
    List<Service> getServicesByProfessionalIdAndState(String professionalId, ServiceStateEnum state);
    List<Service> getServicesByUrgency();
    void deleteServicesForClient(String clientId);
    void deleteServicesForProfessional(String professionalId);
    boolean serviceExists(String serviceId);

    Service getById(String id);

    Service createService(Service service);

    List<Service> getServices(String filter, String sort);

    Service updateService(String id, Service service);

    void deleteService(String id);
}
