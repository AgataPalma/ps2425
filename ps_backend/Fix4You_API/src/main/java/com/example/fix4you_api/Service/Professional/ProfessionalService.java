package com.example.fix4you_api.Service.Professional;

import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Service.Professional.DTOs.ProfessionalData;

import java.util.List;
import java.util.Map;

public interface ProfessionalService {
    List<Professional> getProfessionals(String filter, String sort);

    ProfessionalData getAllProfessionalsCompleteData(String id);

    Professional getProfessionalById(String id);

    Professional createProfessional(Professional professional);

    Professional updateProfessional(String id, Professional professional);

    Professional partialUpdateProfessional(String id, Map<String, Object> updates);

    Professional deleteProfessional(String id);

    boolean nifExists(String nif);

    void setRating(float rating, Professional professional);
}
