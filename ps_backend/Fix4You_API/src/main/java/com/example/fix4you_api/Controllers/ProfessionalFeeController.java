package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Models.ProfessionalsFee;
import com.example.fix4you_api.Service.ProfessionalsFee.ProfessionalsFeeService;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("professionalFees")
@RequiredArgsConstructor
public class ProfessionalFeeController {

    private final ProfessionalsFeeService professionalsFeeService;

    @PostMapping
    public ResponseEntity<ProfessionalsFee> createProfessionalFee(@RequestBody ProfessionalsFee professionalsFee) {
        ProfessionalsFee createdProfessionalsFee = professionalsFeeService.createProfessionalsFee(professionalsFee);
        return new ResponseEntity<>(createdProfessionalsFee, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProfessionalsFee>> getProfessionalFee() {
        List<ProfessionalsFee> professionalsFee = professionalsFeeService.getAllProfessionalsFee();
        return new ResponseEntity<>(professionalsFee, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<ProfessionalsFee>> getUserProfessionalFee(@PathVariable("id") String professionalId) {
        List<ProfessionalsFee> professionalsFee = professionalsFeeService.getProfessionalsFeeForProfessionalId(professionalId);
        return new ResponseEntity<>(professionalsFee, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessionalsFee> getProfessionalFee(@PathVariable String id) {
        ProfessionalsFee professionalsFee = professionalsFeeService.getProfessionalsFeeById(id);
        return new ResponseEntity<>(professionalsFee, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessionalFee(@PathVariable String id) {
        professionalsFeeService.deleteProfessionalFee(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfessionalsFee> updateProfessionalFee(@PathVariable String id, @RequestBody ProfessionalsFee professionalFee) {
        ProfessionalsFee updatedProfessionalsFee = professionalsFeeService.updateProfessionalsFee(id, professionalFee);
        return new ResponseEntity<>(updatedProfessionalsFee, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProfessionalsFee> partialUpdateProfessionalsFee(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        ProfessionalsFee updatedProfessionalsFee = professionalsFeeService.partialUpdateProfessionalsFee(id, updates);
        return new ResponseEntity<>(updatedProfessionalsFee, HttpStatus.OK);
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<ProfessionalsFee> setFeeAsPaid(@PathVariable String id) throws DocumentException {
        ProfessionalsFee fee = professionalsFeeService.setFeeAsPaid(id);
        return new ResponseEntity<>(fee, HttpStatus.OK);
    }

}