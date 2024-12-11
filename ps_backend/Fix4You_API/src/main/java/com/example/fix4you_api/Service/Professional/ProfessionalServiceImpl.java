package com.example.fix4you_api.Service.Professional;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Models.*;
import com.example.fix4you_api.Data.MongoRepositories.CategoryDescriptionRepository;
import com.example.fix4you_api.Data.MongoRepositories.PortfolioItemRepository;
import com.example.fix4you_api.Data.MongoRepositories.ProfessionalRepository;
import com.example.fix4you_api.Service.Category.CategoryService;
import com.example.fix4you_api.Service.Professional.DTOs.ProfessionalData;
import com.example.fix4you_api.Rsql.RsqlQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class ProfessionalServiceImpl implements ProfessionalService {

    private final ProfessionalRepository professionalRepository;
    private final CategoryDescriptionRepository categoryDescriptionRepository;
    private final PortfolioItemRepository portfolioItemRepository;

    private final CategoryService categoryService;
    private final RsqlQueryService rsqlQueryService;

    @Override
    public List<Professional> getProfessionals(String filter, String sort) {
        if (isEmpty(filter) && isEmpty(sort)) {
            return professionalRepository.findByUserType(EnumUserType.PROFESSIONAL);
        }

        if (filter.contains("category")) {
            String categoryName = extractCategoryFromFilter(filter);
            Category category = categoryService.getCategoryByName(categoryName);

            List<String> professionalIds = rsqlQueryService.getProfessionalIdsByCategory(category.getId());

            if(professionalIds.isEmpty()) {
                professionalIds.add("null");
            }

            String categoryFilter = "id=in=(" + String.join(",", professionalIds) + ")";
            filter = filter.replaceAll("category==\"[^\"]+\"", categoryFilter);

        }

        if (filter.contains("availability")) {
            String targetDateStr = extractAvailabilityFromFilter(filter);
            LocalDateTime targetDate = LocalDateTime.parse(targetDateStr);

            List<String> unavailableProfessionalIds  = rsqlQueryService.getUnavailableProfessionalIdsByAvailability(targetDate);

            if(unavailableProfessionalIds.isEmpty()) {
                unavailableProfessionalIds.add("null");
            }

            String availabilityFilter = "id=out=(" + String.join(",", unavailableProfessionalIds) + ")";
            filter = filter.replaceAll("availability==\"[^\"]+\"", availabilityFilter);

        }

        filter = !isEmpty(filter)
                ? "(" + filter + ");userType==\"PROFESSIONAL\""
                : "userType==\"PROFESSIONAL\"";

        return rsqlQueryService.findAll(Professional.class, filter, sort);
    }

    @Override
    public List<Professional> getAllActiveProfessionals() {
        return professionalRepository.findActiveProfessionalsByUserType(EnumUserType.PROFESSIONAL);
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
                        professional.getDescription(),
                        professional.getNif(),
                        professional.getLanguages(),
                        //professional.getLocationsRange(),
                        professional.getAcceptedPayments(),
                        categoriesProfessional,
                        portfolioItems,
                        professional.getRating()
                );

                return data;
            }
        }
        return null;
    }

    @Override
    public Professional getProfessionalById(String id) {
        return findOrThrow(id);
    }

    @Override
    public Professional getProfessionalByIdNotThrow(String id) {
        return professionalRepository.findById(id).orElse(null);
    }

    @Override
    public Professional createProfessional(Professional professional) {
        professional.setUserType(EnumUserType.PROFESSIONAL);
        professional.setDateCreation(LocalDateTime.now());
        professional.setStrikes(0);
        professional.setIsEmailConfirmed(true);
        professional.setRating(0);
        professional.setSuspended(false);
        professional.setSuspensionReason("");

        return professionalRepository.save(professional);
    }

    @Override
    @Transactional
    public Professional updateProfessional(String id, Professional professional) {
        Professional existingProfessional = findOrThrow(id);
        BeanUtils.copyProperties(professional, existingProfessional, "id", "dateCreation", "strikes", "rating");
        return professionalRepository.save(existingProfessional);
    }

    @Override
    @Transactional
    public Professional updateProfessionalImage(String id, byte[] profileImage){
        Professional professional = findOrThrow(id);
        professional.setProfileImage(profileImage);

        return professionalRepository.save(professional);
    }

    @Override
    @Transactional
    public Professional partialUpdateProfessional(String id, Map<String, Object> updates) {
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Professional não encontrado!"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "email" -> professional.setEmail((String) value);
                case "password" -> professional.setPassword((String) value);
                case "userType" -> {
                    try {
                        professional.setUserType(EnumUserType.valueOf(value.toString().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Valor inválido para o estado: " + value);
                    }
                }
                case "name" -> professional.setName((String) value);
                case "phoneNumber" -> professional.setPhoneNumber((String) value);
                case "languages" -> professional.setLanguages((List<Language>) value);
                case "description" -> professional.setDescription((String) value);
                case "location" -> professional.setLocation((String) value);
                //case "locationsRange" -> professional.setLocationsRange((Integer) value);
                case "acceptedPayments" -> professional.setAcceptedPayments((List<PaymentMethod>) value);
                case "strikes" -> professional.setStrikes((Integer) value);
                case "isSuspended" -> professional.setSuspended((boolean) value);
                case "suspensionReason" -> professional.setSuspensionReason((String) value);
                default -> throw new RuntimeException("Campo inválido no pedido da atualização!");
            }
        });

        return professionalRepository.save(professional);
    }

    @Override
    @Transactional
    public void deleteProfessional(String id) {
        //Professional existingProfessional = findOrThrow(id);
        //existingProfessional.setIsDeleted(true);
        //return professionalRepository.save(existingProfessional);

        professionalRepository.deleteById(id);
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

    @Override
    public void setRating(float rating, Professional professional){
        professional.setRating(rating);
        professionalRepository.save(professional);
    }

    @Override
    public void setProfessionalIsSuspended(String professionalId, boolean isSuspended, String suspensionReason) {
        Professional existingProfessional = findOrThrow(professionalId);
        existingProfessional.setSuspended(isSuspended);
        if(isSuspended)
            existingProfessional.setSuspensionReason(suspensionReason);
        else
            existingProfessional.setSuspensionReason("");
        professionalRepository.save(existingProfessional);
    }

    public List<String> getProfessionalCategories(String professionalId){
        List<CategoryDescription> categoryDescriptions = categoryDescriptionRepository.findByProfessionalId(professionalId);
        List<String> categoriesNames = new ArrayList<>();

        for(CategoryDescription categoryDescription : categoryDescriptions) {
            categoriesNames.add(categoryDescription.getCategory().getName());
        }

        return categoriesNames;
    }

    private Professional findOrThrow(String id) {
        return professionalRepository.findById(id)
                .filter(professional -> professional.getUserType() == EnumUserType.PROFESSIONAL)
                .orElseThrow(() -> new NoSuchElementException(String.format("Professional %s não encontrado! Ou o utilizador não é um professional", id)));
    }

    private String extractCategoryFromFilter(String filter) {
        String regex = "category==\"([^\"]+)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(filter);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String extractAvailabilityFromFilter(String filter) {
        String regex = "availability==\"([^\"]+)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(filter);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}