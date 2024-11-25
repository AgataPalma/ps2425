package com.example.fix4you_api.Service.Service;

import com.example.fix4you_api.Data.Enums.ServiceStateEnum;
import com.example.fix4you_api.Data.Models.*;

import java.util.List;

public interface ServiceService {
    Service getById(String id);
    Service createService(Service service);
    List<Service> getServices(String filter, String sort);
    List<ClientServiceCount> getTopActivitiesClients();
    List<ProfessionalServiceCount> getTopActivitiesProfessionals();
    List<ClientTotalSpent> getTopPriceClients();
    Service updateService(String id, Service service);
    List<Service> getServicesByProfessionalId(String professionalId);
    List<Service> getServicesByClientId(String clientId);
    List<Service> getServicesByProfessionalIdAndState(String professionalId, ServiceStateEnum state);
    List<Service> getServicesByUrgency();
    void deleteService(String id);
    void deleteServicesForClient(String clientId);
    void deleteServicesForProfessional(String professionalId);
    boolean serviceExists(String serviceId);
}
