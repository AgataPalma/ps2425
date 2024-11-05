package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Models.CategoryDescription;
import com.example.fix4you_api.Data.MongoRepositories.CategoryDescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/categoryDescriptions")
public class CategoryDescriptionController {

    @Autowired
    private CategoryDescriptionRepository categoryDescriptionRepository;

    @Autowired
    public CategoryDescriptionController(CategoryDescriptionRepository categoryDescriptionRepository) {
        this.categoryDescriptionRepository = categoryDescriptionRepository;
    }

    @PostMapping
    public ResponseEntity<String> addCategoryDescription(@RequestBody CategoryDescription categoryDescription) {
        try {
            this.categoryDescriptionRepository.save(categoryDescription);
            return ResponseEntity.ok("Category Description Added!");
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getCategoryDescription() {
        try {
            List<CategoryDescription> categoryDescriptions = this.categoryDescriptionRepository.findAll();
            return ResponseEntity.ok(categoryDescriptions);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserCategoryDescriptions(@PathVariable("id") String idProfessional) {
        try {
            List<CategoryDescription> categoryDescriptions = this.categoryDescriptionRepository.findByProfessionalId(idProfessional);
            return ResponseEntity.ok(categoryDescriptions);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryDescription(@PathVariable String id) {
        try {
            Optional<CategoryDescription> categoryDescriptions = this.categoryDescriptionRepository.findById(id);
            return (categoryDescriptions.isPresent() ? ResponseEntity.ok(categoryDescriptions.get()) : ResponseEntity.ok("Couldn't find any Category Description with the id: '" + id + "'!"));
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategoryDescription(@PathVariable String id) {
        try {
            Optional<CategoryDescription> categoryDescription = this.categoryDescriptionRepository.findById(id);
            this.categoryDescriptionRepository.deleteById(id);
            String msg = (categoryDescription.isPresent() ? "Category description with id '" + id + "' was deleted!" : "Couldn't find any category description with the id: '" + id + "'!");
            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There was an error trying to delete the category description with id: '" + id + "'!");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategoryDescription(@PathVariable String id, @RequestBody CategoryDescription categoryDescription) {
        try {
            Optional<CategoryDescription> categoryDescriptionOpt = this.categoryDescriptionRepository.findById(id);
            if (categoryDescriptionOpt.isPresent()) {
                this.categoryDescriptionRepository.save(categoryDescription);
                return ResponseEntity.ok(categoryDescription);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Couldn't find any category description with the id: '" + id + "'!");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}