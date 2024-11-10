package com.example.fix4you_api.Service.Service;

import com.example.fix4you_api.Data.Models.Service;

import java.util.List;

public interface ServiceService {
    List<Service> getServicesByProfessionalId(String professionalId);
    List<Service> getServicesByClientId(String clientId);
    void deleteServicesForClient(String clientId);
    void deleteServicesFroProfessional(String professionalId);
    boolean serviceExists(String serviceId);
}
