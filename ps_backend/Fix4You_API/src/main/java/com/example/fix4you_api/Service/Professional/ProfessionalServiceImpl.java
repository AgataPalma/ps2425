package com.example.fix4you_api.Service.Professional;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Enums.LanguageEnum;
import com.example.fix4you_api.Data.Enums.PaymentTypesEnum;
import com.example.fix4you_api.Data.Models.CategoryDescription;
import com.example.fix4you_api.Data.Models.PortfolioItem;
import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.Models.User;
import com.example.fix4you_api.Data.MongoRepositories.CategoryDescriptionRepository;
import com.example.fix4you_api.Data.MongoRepositories.PortfolioItemRepository;
import com.example.fix4you_api.Data.MongoRepositories.ProfessionalRepository;
import com.example.fix4you_api.Service.Professional.DTOs.ProfessionalCategoryData;
import com.example.fix4you_api.Service.Professional.DTOs.ProfessionalData;
import com.example.fix4you_api.Rsql.RsqlQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class ProfessionalServiceImpl implements ProfessionalService {

    private final ProfessionalRepository professionalRepository;
    private final CategoryDescriptionRepository categoryDescriptionRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final RsqlQueryService rsqlQueryService;

    @Override
    public List<Professional> getProfessionals(String filter, String sort) {
        if (isEmpty(filter) && isEmpty(sort)) {
            return professionalRepository.findByUserType(EnumUserType.PROFESSIONAL);
        }
        filter = !isEmpty(filter)
                ? "(" + filter + ");userType==\"PROFESSIONAL\""
                : "userType==\"PROFESSIONAL\"";

        return rsqlQueryService.findAll(Professional.class, filter, sort);
    }

    @Override
    public ProfessionalData getAllProfessionalsCompleteData(String id){
        List<Professional> professionalList = professionalRepository.findByUserType(EnumUserType.PROFESSIONAL);
        //List<ProfessionalData> professionalsData = new ArrayList<>();

        for (Professional professional : professionalList) {
            if(professional.getId().equals(id)){
                List<CategoryDescription> categoriesProfessional = new ArrayList<>();
                categoriesProfessional = categoryDescriptionRepository.findByProfessionalId(professional.getId());
                List<PortfolioItem> portfolioItems = portfolioItemRepository.findByProfessionalId(professional.getId());

                ProfessionalData data = new ProfessionalData(
                        professional.getId(),
                        professional.getEmail(),
                        professional.getDateCreation(),
                        professional.getUserType(),
                        professional.getName(),
                        professional.getPhoneNumber(),
                        professional.getLocation(),
                        professional.getProfileImage(),
                        professional.getDescription(),
                        professional.getNif(),
                        professional.getLanguages(),
                        professional.getLocationsRange(),
                        professional.getAcceptedPayments(),
                        categoriesProfessional,
                        portfolioItems
                );

                return data;
            }
        }
        return null;
    }

    @Override
    public List<ProfessionalCategoryData> getAllProfessionalsCategoryData() {
        List<CategoryDescription> categoriesProfessional = categoryDescriptionRepository.findAll();

        List<ProfessionalCategoryData> data = new ArrayList<>();

        for (CategoryDescription categoryDescription : categoriesProfessional) {
            Professional professional = getProfessionalById(categoryDescription.getProfessionalId());
            if(professional == null) continue;

            ProfessionalCategoryData professionalCategoryData = new ProfessionalCategoryData(
                    professional.getId(),
                    professional.getEmail(),
                    professional.getDateCreation(),
                    professional.getUserType(),
                    professional.getName(),
                    professional.getPhoneNumber(),
                    professional.getLocation(),
                    professional.getProfileImage(),
                    professional.getDescription(),
                    professional.getNif(),
                    professional.getLanguages(),
                    professional.getLocationsRange(),
                    professional.getAcceptedPayments(),
                    categoryDescription.getCategory(),
                    categoryDescription.isChargesTravels(),
                    categoryDescription.isProvidesInvoices(),
                    categoryDescription.getMediumPricePerService()
            );

            data.add(professionalCategoryData);
        }

        return data;
    }

    @Override
    public Professional getProfessionalById(String id) {
        return findOrThrow(id);
    }

    @Override
    public Professional createProfessional(Professional professional) {
        professional.setDateCreation(LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.of("UTC")));
        professional.setStrikes(0);
        professional.setIsEmailConfirmed(true);
        return professionalRepository.save(professional);
    }

    @Override
    @Transactional
    public Professional updateProfessional(String id, Professional professional) {
        Professional existingProfessional = findOrThrow(id);

        existingProfessional.setEmail(professional.getEmail());
        existingProfessional.setPassword(professional.getPassword());
        existingProfessional.setDateCreation(professional.getDateCreation());
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
        existingProfessional.setStrikes(professional.getStrikes());
        existingProfessional.setIsEmailConfirmed(professional.isIsEmailConfirmed());

        return professionalRepository.save(existingProfessional);
    }

    @Override
    @Transactional
    public Professional partialUpdateProfessional(String id, Map<String, Object> updates) {
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Professional not found"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "password" -> professional.setPassword((String) value);
                case "name" -> professional.setName((String) value);
                case "phoneNumber" -> professional.setPhoneNumber((String) value);
                case "languages" -> professional.setLanguages((List<LanguageEnum>) value);
                case "profileImage" -> professional.setProfileImage((byte[]) value);
                case "description" -> professional.setDescription((String) value);
                case "location" -> professional.setLocation((String) value);
                case "locationsRange" -> professional.setLocationsRange((Integer) value);
                case "acceptedPayments" -> professional.setAcceptedPayments((List<PaymentTypesEnum>) value);
                case "strikes" -> professional.setStrikes((Integer) value);
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


    @Override
    public boolean nifExists(String nif){
        List<Professional> professionals = getProfessionals("","");

        for(Professional professional : professionals) {
            if(professional.getNif().equals(nif))
                return true;
        }

        return false;
    }

    private Professional findOrThrow(String id) {
        return professionalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Professional %s not found", id)));
    }

}
