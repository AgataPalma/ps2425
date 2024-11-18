package com.example.fix4you_api.Service.Professional;

import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Service.Professional.DTOs.ProfessionalData;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ProfessionalService {
    List<Professional> getProfessionals(String filter, String sort);

    List<Professional> getAllActiveProfessionals();

    ProfessionalData getAllProfessionalsCompleteData(String id);

    Professional getProfessionalById(String id);

    Professional createProfessional(Professional professional) throws IOException;

    Professional updateProfessional(String id, Professional professional) throws IOException;

    Professional updateProfessionalImage(String id, byte[] profileImage);

    Professional partialUpdateProfessional(String id, Map<String, Object> updates);

    Professional deleteProfessional(String id);

    void setProfessionalIsSuspended(String professionalId, boolean isSuspended);

    boolean nifExists(String nif);

    void setRating(float rating, Professional professional);
}
