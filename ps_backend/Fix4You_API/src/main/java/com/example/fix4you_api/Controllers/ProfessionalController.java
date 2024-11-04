package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.MongoRepositories.ProfessionalRepository;
import com.example.fix4you_api.Service.Professional.ProfessionalService;
import com.example.fix4you_api.Service.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("professionals")
public class ProfessionalController {
    private ProfessionalService professionalService;
    private UserService userService;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    public ProfessionalController(ProfessionalService professionalService, ProfessionalRepository professionalRepository, UserService userService) {
        this.professionalService = professionalService;
        this.professionalRepository = professionalRepository;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createProfessional(@RequestBody Professional professional) {
        Professional response = professionalService.createProfessional(professional);
        if(response == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // send verification email
        //userService.sendValidationEmailUserRegistration(professional.getEmail());

        return ResponseEntity.ok("Professional created! Email confirmation sent.");
    }

    @GetMapping
    public ResponseEntity<?> getProfessionals() {
        List<Professional> professionals = professionalService.getAllProfessionals();
        if (professionals.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No professionals found.");
        }
        return ResponseEntity.ok(professionals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfessional(@PathVariable("id") String id) {
        return ResponseEntity.ok(professionalService.getProfessionalById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProfessional(@PathVariable String id) {
        professionalService.deleteProfessional(id);
        return ResponseEntity.ok(String.format("Professional %s deleted", id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfessional(@PathVariable String id, @RequestBody Professional professional) {
        return ResponseEntity.ok(professionalService.updateProfessional(id, professional));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateProfessional(
            @PathVariable String id,
            @RequestBody Map<String, Object> updates) {
        try {
            Professional updatedProfessional = professionalService.partialUpdateProfessional(id, updates);
            return ResponseEntity.ok(updatedProfessional);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}