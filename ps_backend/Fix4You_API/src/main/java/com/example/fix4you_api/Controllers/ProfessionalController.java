package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Models.CategoryDescription;
import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.Models.ProfessionalRegistrationRequest;
import com.example.fix4you_api.Service.CategoryDescription.CategoryDescriptionService;
import com.example.fix4you_api.Service.Professional.ProfessionalService;
import com.example.fix4you_api.Service.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("professionals")
@RequiredArgsConstructor
public class ProfessionalController {

    private final ProfessionalService professionalService;
    private final CategoryDescriptionService categoryDescriptionService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Professional> createProfessional(@RequestBody ProfessionalRegistrationRequest professionalRegistrationRequest) {
        Professional createdProfessioanl = professionalService.createProfessional(professionalRegistrationRequest.getProfessional());

        professionalRegistrationRequest.getCategoryDescriptions().forEach(categoryDescription -> {
            categoryDescription.setProfessionalId(createdProfessioanl.getId());
            categoryDescriptionService.createCategoryDescription(categoryDescription);
        });

        // send verification email
        userService.sendValidationEmailUserRegistration(createdProfessioanl.getEmail());
        return new ResponseEntity<>(createdProfessioanl, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Professional>> getAllProfessionals() {
        List<Professional> professionals = professionalService.getAllProfessionals();
        return new ResponseEntity<>(professionals, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Professional> getProfessionalById(@PathVariable("id") String id) {
        Professional professional = professionalService.getProfessionalById(id);
        return new ResponseEntity<>(professional, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Professional> updateProfessional(@PathVariable String id, @RequestBody Professional professional) {
        Professional updatedProfessional = professionalService.updateProfessional(id, professional);
        return new ResponseEntity<>(updatedProfessional, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Professional> partialUpdateProfessional(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        Professional updatedProfessional = professionalService.partialUpdateProfessional(id, updates);
        return new ResponseEntity<>(updatedProfessional, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessional(@PathVariable String id) {
        professionalService.deleteProfessional(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}