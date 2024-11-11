package com.example.fix4you_api.Service.Professional;

import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Service.Professional.DTOs.ProfessionalCategoryData;
import com.example.fix4you_api.Service.Professional.DTOs.ProfessionalData;

import java.util.List;
import java.util.Map;

public interface ProfessionalService {
    List<Professional> getProfessionals(String filter, String sort);

    ProfessionalData getAllProfessionalsCompleteData(String id);

    // its used for client dashboard
    List<ProfessionalCategoryData> getAllProfessionalsCategoryData();

    Professional getProfessionalById(String id);

    Professional getProfessionalByIdNoException(String id);

    Professional createProfessional(Professional professional);

    Professional updateProfessional(String id, Professional professional);

    Professional partialUpdateProfessional(String id, Map<String, Object> updates);

    void deleteProfessional(String id);

    boolean nifExists(String nif);

    void setRating(float rating, Professional professional);
}
