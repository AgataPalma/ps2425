package com.example.fix4you_api.Service.Professional;

import com.example.fix4you_api.Data.Enums.PaymentTypesEnum;
import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.MongoRepositories.ProfessionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProfessionalServiceImpl implements ProfessionalService {

    private final ProfessionalRepository professionalRepository;

    @Override
    public List<Professional> getAllProfessionals() {
        return professionalRepository.findAll();
    }

    @Override
    public Professional getProfessionalById(String id) {
        return findOrThrow(id);
    }

    @Override
    public Professional createProfessional(Professional professional) {
        LocalDateTime dateTime = LocalDateTime.now();
        professional.setDateCreation(dateTime);
        return professionalRepository.save(professional);
    }

    @Override
    @Transactional
    public Professional updateProfessional(String id, Professional professional) {
        Professional existingProfessional = findOrThrow(id);
        LocalDateTime dateTime = LocalDateTime.now();

        existingProfessional.setEmail(professional.getEmail());
        existingProfessional.setPassword(professional.getPassword());
        professional.setDateCreation(dateTime);
        existingProfessional.setUserType(professional.getUserType());
        existingProfessional.setName(professional.getName());
        existingProfessional.setPhoneNumber(professional.getPhoneNumber());
        existingProfessional.setLanguages(professional.getLanguages());
        existingProfessional.setProfileImage(professional.getProfileImage());
        existingProfessional.setAgeValidation(professional.isAgeValidation());
        existingProfessional.setDescription(professional.getDescription());
        existingProfessional.setNif(professional.getNif());
        existingProfessional.setLocation(professional.getLocation());
        existingProfessional.setLocationsRange(professional.getLocationsRange());
        existingProfessional.setAcceptedPayments(professional.getAcceptedPayments());
        existingProfessional.setCompany(professional.isCompany());

        return professionalRepository.save(existingProfessional);
    }

    @Override
    @Transactional
    public Professional partialUpdateProfessional(String id, Map<String, Object> updates) {
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Professional not found"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "description" -> professional.setDescription((String) value);
                case "nif" -> professional.setNif((String) value);
                case "location" -> professional.setLocation((String) value);
                case "locationsRange" -> professional.setLocationsRange((Integer) value);
                case "acceptedPayments" -> professional.setAcceptedPayments((List<PaymentTypesEnum>) value);
                case "isCompany" -> professional.setCompany((Boolean) value);
                default -> throw new RuntimeException("Invalid field update request");
            }
        });

        return professionalRepository.save(professional);
    }

    @Override
    @Transactional
    public void deleteProfessional(String id) {
        Professional professional = findOrThrow(id);
        professionalRepository.delete(professional);
    }

    private Professional findOrThrow(String id) {
        return professionalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Professional %s not found", id)));
    }

}
