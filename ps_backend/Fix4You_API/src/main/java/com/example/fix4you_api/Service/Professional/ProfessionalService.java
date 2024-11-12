package com.example.fix4you_api.Service.Professional;

import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Service.Professional.DTOs.ProfessionalData;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ProfessionalService {
    List<Professional> getProfessionals(String filter, String sort);

    ProfessionalData getAllProfessionalsCompleteData(String id);

    Professional getProfessionalById(String id);

    Professional createProfessional(Professional professional) throws IOException;

    Professional updateProfessional(Professional professional) throws IOException;

    Professional partialUpdateProfessional(String id, Map<String, Object> updates);

    void deleteProfessional(String id);

    boolean nifExists(String nif);

    void setRating(float rating, Professional professional);
}
