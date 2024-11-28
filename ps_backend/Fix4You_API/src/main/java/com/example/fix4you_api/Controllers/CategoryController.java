package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Models.Category;
import com.example.fix4you_api.Data.Models.Dtos.SimpleCategoryDTO;
import com.example.fix4you_api.Service.Category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody SimpleCategoryDTO category) {
        if(categoryService.nameExists(category.getName())){
            return new ResponseEntity<>("O nome da categoria já existe!", HttpStatus.CONFLICT);
        }

        Category newCategory = new Category();
        newCategory.setName(category.getName());
        newCategory.setMaxValue(0f);
        newCategory.setMinValue(0f);

        Category createdCategory = categoryService.createCategory(newCategory);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable String id, @RequestBody Category category) {
        Category updatedCategory = categoryService.updateCategory(id, category);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Category> partialUpdateCategory(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        Category updatedCategory = categoryService.partialUpdateCategory(id, updates);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessional(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}