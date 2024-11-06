package com.example.fix4you_api.Service.Professional;

import com.example.fix4you_api.Data.Models.Professional;

import java.util.List;
import java.util.Map;

public interface ProfessionalService {
    List<Professional> getProfessionals(String filter, String sort);

    Professional getProfessionalById(String id);

    Professional createProfessional(Professional professional);

    Professional updateProfessional(String id, Professional professional);

    Professional partialUpdateProfessional(String id, Map<String, Object> updates);

    void deleteProfessional(String id);

    boolean nifExists(String nif);
}
