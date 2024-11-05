package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Models.ProfessionalsFee;
import com.example.fix4you_api.Data.MongoRepositories.ProfessionalFeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("professionalFees")
public class ProfessionalFeeController {
    @Autowired
    private ProfessionalFeeRepository professionalFeeRepository;

    @Autowired
    public ProfessionalFeeController(ProfessionalFeeRepository professionalFeeRepository) {
        this.professionalFeeRepository = professionalFeeRepository;
    }

    @PostMapping
    public ResponseEntity<String> addProfessionalFee(@RequestBody ProfessionalsFee professionalsFee) {
        try {
            LocalDateTime paymentDate = LocalDateTime.now();
            professionalsFee.setPaymentDate(paymentDate);
            this.professionalFeeRepository.save(professionalsFee);
            return ResponseEntity.ok("Professional Fee Added!");
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getProfessionalFee() {
        try {
            List<ProfessionalsFee> professionalsFee = this.professionalFeeRepository.findAll();
            return ResponseEntity.ok(professionalsFee);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserProfessionalFee(@PathVariable("id") String idProfessional) {
        try {
            List<ProfessionalsFee> professionalsFee = this.professionalFeeRepository.findByProfessionalId(idProfessional);
            return ResponseEntity.ok(professionalsFee);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfessionalFee(@PathVariable String id) {
        try {
            Optional<ProfessionalsFee> professionalsFee = this.professionalFeeRepository.findById(id);
            return (professionalsFee.isPresent() ? ResponseEntity.ok(professionalsFee.get()) : ResponseEntity.ok("Couldn't find any professional fee with the id: '" + id + "'!"));
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProfessionalFee(@PathVariable String id) {
        try {
            Optional<ProfessionalsFee> professionalsFee = this.professionalFeeRepository.findById(id);
            this.professionalFeeRepository.deleteById(id);
            String msg = (professionalsFee.isPresent() ? "Professional fee with id '" + id + "' was deleted!" : "Couldn't find any professional fee with the id: '" + id + "'!");
            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There was an error trying to delete the professional fee with id: '" + id + "'!");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfessionalFee(@PathVariable String id, @RequestBody ProfessionalsFee professionalFee) {
        try {
            Optional<ProfessionalsFee> professionalsFeeOpt = this.professionalFeeRepository.findById(id);
            if (professionalsFeeOpt.isPresent()) {
                LocalDateTime paymentDate = LocalDateTime.now();
                professionalFee.setPaymentDate(paymentDate);
                this.professionalFeeRepository.save(professionalFee);
                return ResponseEntity.ok(professionalFee);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Couldn't find any professional fee with the id: '" + id + "'!");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
