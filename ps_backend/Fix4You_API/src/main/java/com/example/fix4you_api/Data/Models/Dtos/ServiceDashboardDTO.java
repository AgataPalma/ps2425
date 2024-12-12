package com.example.fix4you_api.Data.Models.Dtos;

import com.example.fix4you_api.Data.Models.Service;

public class ServiceDashboardDTO extends Service {
    private byte[] clientProfileImage;

    public ServiceDashboardDTO(Service service) {
        this.setId(service.getId());
        this.setClientId(service.getClientId());
        this.setProfessionalId(service.getProfessionalId());
        this.setPrice(service.getPrice());
        this.setAddress(service.getAddress());
        this.setPostalCode(service.getPostalCode());
        this.setCategory(service.getCategory());
        this.setDescription(service.getDescription());
        this.setTitle(service.getTitle());
        this.setState(service.getState());
        this.setUrgent(service.isUrgent());
        this.setDateCreation(service.getDateCreation());
        this.setAgreementDate(service.getAgreementDate());
        this.setLanguages(service.getLanguages());
        this.setLocation(service.getLocation());
    }

    public byte[] getClientProfileImage() {
        return clientProfileImage;
    }

    public void setClientProfileImage(byte[] clientProfileImage) {
        this.clientProfileImage = clientProfileImage;
    }
}
