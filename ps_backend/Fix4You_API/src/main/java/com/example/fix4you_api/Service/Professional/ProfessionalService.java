package com.example.fix4you_api.Service.Professional;

import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Service.Professional.DTOs.ProfessionalData;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ProfessionalService {
    List<Professional> getProfessionals(String filter, String sort);

    List<Professional> getAllActiveProfessionals();

    List<Professional> getProfessionalsByLanguage(String languageId);

    List<Professional> getProfessionalsByPaymentMethod(String paymentMethodId);

    ProfessionalData getAllProfessionalsCompleteData(String id);

    Professional getProfessionalById(String id);

    Professional createProfessional(Professional professional) throws IOException;

    Professional updateProfessional(String id, Professional professional) throws IOException;

    Professional updateProfessionalImage(String id, byte[] profileImage);

    Professional partialUpdateProfessional(String id, Map<String, Object> updates);

    void deleteProfessional(String id);

    void setProfessionalIsSuspended(String professionalId, boolean isSuspended, String suspensionReason);

    boolean nifExists(String nif);

    void setRating(float rating, Professional professional);

    List<String> getProfessionalCategories(String professionalId);

    Professional getProfessionalByIdNotThrow(String id);
}
