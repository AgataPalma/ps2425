package com.example.fix4you_api.Service.Category;

import com.example.fix4you_api.Data.Models.Category;
import com.example.fix4you_api.Data.Models.CategoryDescription;
import com.example.fix4you_api.Data.Models.Dtos.LowestPriceEntryDTO;
import com.example.fix4you_api.Data.MongoRepositories.CategoryRepository;
import com.example.fix4you_api.Service.CategoryDescription.CategoryDescriptionService;
import com.example.fix4you_api.Service.Service.ServiceService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryDescriptionService categoryDescriptionService;
    private final ServiceService serviceService;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               @Lazy CategoryDescriptionService categoryDescriptionService,
                               @Lazy ServiceService service) {
        this.categoryRepository = categoryRepository;
        this.categoryDescriptionService = categoryDescriptionService;
        this.serviceService = service;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryByName(String categoryName) {
        return categoryRepository.findByName(categoryName);
    }

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(String id, Category category) {
        List<com.example.fix4you_api.Data.Models.Service> services =  serviceService.getServicesByCategoryId(id);
        List<CategoryDescription> categoryDescriptions = categoryDescriptionService.getCategoriesDescriptionByCategoryId(id);

        if (services.isEmpty() && categoryDescriptions.isEmpty()) {
            // Se não houver serviços e descrições de categoria para os profissionais associadas, exclui a categoria
            Category existingCategory = findOrThrow(id);
            BeanUtils.copyProperties(category, existingCategory, "id");
            return categoryRepository.save(existingCategory);
        } else {
            throw new IllegalStateException("Não é possível editar totalmente a categoria, pois ela possui serviços ou profissionais associados.");
        }
    }

    @Override
    @Transactional
    public Category partialUpdateCategory(String id, Map<String, Object> updates) {
        Category existingCategory = findOrThrow(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> {
                    List<com.example.fix4you_api.Data.Models.Service> services =  serviceService.getServicesByCategoryId(id);
                    List<CategoryDescription> categoryDescriptions = categoryDescriptionService.getCategoriesDescriptionByCategoryId(id);

                    if (services.isEmpty() && categoryDescriptions.isEmpty()) {
                        // Se não houver serviços e descrições de categoria para os profissionais associadas, exclui a categoria
                        existingCategory.setName((String) value);
                    } else {
                        throw new IllegalStateException("Não é possível editar o nome da categoria, pois ela possui serviços ou profissionais associados.");
                    }
                }
                case "minValue" -> existingCategory.setMinValue((float) value);
                case "maxValue" -> existingCategory.setMaxValue((float) value);
                default -> throw new RuntimeException("Invalid field update request");
            }
        });

        return categoryRepository.save(existingCategory);
    }

    @Override
    @Transactional
    public void updateCategoryMinMaxValue(String id) {
        List<CategoryDescription> categoryDescriptions = categoryDescriptionService.getCategoriesDescriptionByCategoryId(id);
        Map<String, Object> updates = new HashMap<>();

        if (categoryDescriptions.isEmpty()) {
            updates.put("minValue", 0f);
            updates.put("maxValue", 0f);
        } else {

            double minPrice = categoryDescriptions.stream()
                    .mapToDouble(CategoryDescription::getMediumPricePerService)
                    .min()
                    .orElseThrow(() -> new RuntimeException("Não é possível calcular o preço mínimo!"));

            double maxPrice = categoryDescriptions.stream()
                    .mapToDouble(CategoryDescription::getMediumPricePerService)
                    .max()
                    .orElseThrow(() -> new RuntimeException("Não é possível calcular o preço máximo!"));


            updates.put("minValue", (float) minPrice);
            updates.put("maxValue", (float) maxPrice);
        }

        partialUpdateCategory(id, updates);
    }

    @Override
    @Transactional
    public void deleteCategory(String id) {
        List<com.example.fix4you_api.Data.Models.Service> services =  serviceService.getServicesByCategoryId(id);
        List<CategoryDescription> categoryDescriptions = categoryDescriptionService.getCategoriesDescriptionByCategoryId(id);

        if (services.isEmpty() && categoryDescriptions.isEmpty()) {
            // Se não houver serviços e descrições de categoria para os profissionais associadas, exclui a categoria
            categoryRepository.deleteById(id);
        } else {
            throw new IllegalStateException("Não é possível excluir a categoria, pois ela possui serviços ou profissionais associados.");
        }
    }

    static class SuspiciousEntry {
        private String userId;
        private String categoryName;
        private String price;
        private String lowerBound;

        public SuspiciousEntry(String userId, String categoryName, String price, String lowerBound) {
            this.userId = userId;
            this.categoryName = categoryName;
            this.price = price;
            this.lowerBound = lowerBound;
        }
    }

    @Override
    public List<LowestPriceEntryDTO> getSuspiciousPrices() {

        List<CategoryDescription> categoryDescriptions = categoryDescriptionService.getAllCategoriesDescription();
        List<Category> categories = categoryRepository.findAll();
        List<SuspiciousEntry> suspicious = new ArrayList<>();
        List<LowestPriceEntryDTO> lowestPrices = new ArrayList<>();

        for (Category category : categories) {
            // Filter descriptions for the current category
            List<Float> pricesInCategory = categoryDescriptions.stream()
                    .filter(desc -> desc.getCategory().getId().equals(category.getId()))
                    .map(CategoryDescription::getMediumPricePerService)
                    .collect(Collectors.toList());

            if (pricesInCategory.isEmpty()) continue;

            // Sort prices and calculate Q1, Q3, IQR, and lowerBound
            List<Float> sortedPrices = new ArrayList<>(pricesInCategory);
            Collections.sort(sortedPrices);

            double Q1 = sortedPrices.get(sortedPrices.size() / 4);
            double Q3 = sortedPrices.get((3 * sortedPrices.size()) / 4);
            double IQR = Q3 - Q1;
            double lowerBound = Q1 - 1.5 * IQR;

            // Identify suspicious entries
            categoryDescriptions.stream()
                    .filter(desc -> desc.getCategory().getId().equals(category.getId()))
                    .forEach(desc -> {
                        if (desc.getMediumPricePerService() < lowerBound) {
                            suspicious.add(new SuspiciousEntry(
                                    desc.getProfessionalId(),
                                    category.getName(),
                                    String.format("%.2f", desc.getMediumPricePerService()),
                                    String.format("%.2f", lowerBound)
                            ));
                        }
                    });

            // Find the lowest price and corresponding professional
            double lowestPrice = pricesInCategory.stream()
                    .min(Double::compare)
                    .orElse(Float.MAX_VALUE);

            Optional<CategoryDescription> lowestProfessional = categoryDescriptions.stream()
                    .filter(desc -> desc.getCategory().getId().equals(category.getId())
                            && desc.getMediumPricePerService() == lowestPrice)
                    .findFirst();

            lowestProfessional.ifPresent(desc -> lowestPrices.add(new LowestPriceEntryDTO(
                    desc.getProfessionalId(),
                    category.getName(),
                    String.format("%.2f", desc.getMediumPricePerService())
            )));
        }

        return lowestPrices;
    }

    private Category findOrThrow(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Categoria %s não encontrada!", id)));
    }

    @Override
    public boolean nameExists(String name) {
        List<Category> categories = categoryRepository.findAll();

        for (Category category : categories) {
            if(category.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

}