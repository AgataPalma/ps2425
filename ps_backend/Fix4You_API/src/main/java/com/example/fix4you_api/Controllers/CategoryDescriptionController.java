package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Models.CategoryDescription;
import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Service.Category.CategoryService;
import com.example.fix4you_api.Service.CategoryDescription.CategoryDescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/categoryDescriptions")
@RequiredArgsConstructor
public class CategoryDescriptionController {

    private final CategoryDescriptionService categoryDescriptionService;
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDescription> addCategoryDescription(@RequestBody CategoryDescription categoryDescription) {
        CategoryDescription categoryDescriptionCreated = categoryDescriptionService.createCategoryDescription(categoryDescription);
        categoryService.updateCategoryMinMaxValue(categoryDescriptionCreated.getCategoryId(), categoryDescriptionCreated.getMediumPricePerService());
        return new ResponseEntity<>(categoryDescriptionCreated, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDescription>> getAllCategoriesDescription() {
        List<CategoryDescription> categoryDescriptions = categoryDescriptionService.getAllCategoriesDescription();
        return new ResponseEntity<>(categoryDescriptions, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<CategoryDescription>> getUserCategoryDescriptions(@PathVariable("id") String professionalId) {
        List<CategoryDescription> categoryDescriptions = categoryDescriptionService.getCategoriesDescriptionByProfessionalId(professionalId);
        return new ResponseEntity<>(categoryDescriptions, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDescription> getCategoryDescription(@PathVariable String id) {
            CategoryDescription categoryDescriptions = categoryDescriptionService.getCategoriesDescriptionById(id);
            return new ResponseEntity<>(categoryDescriptions, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDescription> updateCategoryDescription(@PathVariable String id, @RequestBody CategoryDescription categoryDescription) {
        CategoryDescription updatedCategoryDescription = categoryDescriptionService.updatecategoryDescription(id, categoryDescription);
        return new ResponseEntity<>(updatedCategoryDescription, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoryDescription(@PathVariable String id) {
        categoryDescriptionService.deleteCategoryDescriptionsById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}