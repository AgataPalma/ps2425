package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.MongoRepositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("professionals")
public class ProfessionalController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public ProfessionalController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<String> addProfessional(@RequestBody Professional professional) {
        try {
            this.userRepository.save(professional);
            return ResponseEntity.ok("Professional Added!");
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getProfessionals() {
        try {
            List<User> professionals = this.userRepository.findByUserType(EnumUserType.PROFESSIONAL);
            return ResponseEntity.ok(professionals);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfessional(@PathVariable String id) {
        try {
            Optional<User> professionalOpt = this.userRepository.findById(id);
            return (professionalOpt.isPresent() ? ResponseEntity.ok(professionalOpt.get()) : ResponseEntity.ok("Couldn't find any professional with the id: '" + id + "'!"));
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProfessional(@PathVariable String id) {
        try {
            Optional<User> professionalOpt = this.userRepository.findById(id);
            this.userRepository.deleteById(id);
            String msg = (professionalOpt.isPresent() ? "Professional with id '" + id + "' was deleted!" : "Couldn't find any user with the id: '" + id + "'!");
            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There was an error trying to delete the professional with id: '" + id + "'!");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfessional(@PathVariable String id, @RequestBody Professional professional) {
        try {
            Optional<User> professionalOpt = this.userRepository.findById(id);
            if (professionalOpt.isPresent()) {
                this.userRepository.save(professional);
                return ResponseEntity.ok(professional);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Couldn't find any professional with the id: '" + id + "'!");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
