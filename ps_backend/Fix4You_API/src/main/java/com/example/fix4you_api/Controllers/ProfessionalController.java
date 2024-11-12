package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.Models.ProfessionalRegistrationRequest;
import com.example.fix4you_api.Service.CategoryDescription.CategoryDescriptionService;
import com.example.fix4you_api.Service.PortfolioItem.PortfolioItemService;
import com.example.fix4you_api.Service.Professional.DTOs.ProfessionalData;
import com.example.fix4you_api.Service.Professional.ProfessionalService;
import com.example.fix4you_api.Service.ProfessionalsFee.ProfessionalsFeeService;
import com.example.fix4you_api.Service.Review.ReviewService;
import com.example.fix4you_api.Service.Service.ServiceService;
import com.example.fix4you_api.Service.Ticket.TicketService;
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
    private final PortfolioItemService portfolioItemService;
    private final CategoryDescriptionService categoryDescriptionService;
    private final ReviewService reviewService;
    private final ProfessionalsFeeService professionalsFeeService;
    private final TicketService ticketService;
    private final ServiceService serviceService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createProfessional(@RequestBody ProfessionalRegistrationRequest professionalRegistrationRequest) {
        // verify if the email and nif are unique
        if(userService.emailExists(professionalRegistrationRequest.getProfessional().getEmail())){
            return new ResponseEntity<>("Email already exists", HttpStatus.CONFLICT);
        }

        if(professionalService.nifExists(professionalRegistrationRequest.getProfessional().getNif())) {
            return new ResponseEntity<>("NIF already exists", HttpStatus.CONFLICT);
        }

        Professional createdProfessioanl = professionalService.createProfessional(professionalRegistrationRequest.getProfessional());

        professionalRegistrationRequest.getCategoryDescriptions().forEach(categoryDescription -> {
            categoryDescription.setProfessionalId(createdProfessioanl.getId());
            categoryDescriptionService.createCategoryDescription(categoryDescription);
        });

        // send verification email
        //userService.sendValidationEmailUserRegistration(createdProfessioanl.getEmail());
        return new ResponseEntity<>(createdProfessioanl, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Professional>> getProfessionals(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "sort", required = false) String sort) {
        List<Professional> professionals = professionalService.getProfessionals(filter, sort);
        return new ResponseEntity<>(professionals, HttpStatus.OK);
    }

    @GetMapping("/complete-data/{id}")
    public ResponseEntity<ProfessionalData> getProfessionalCompleteData(@PathVariable("id") String id) {
        ProfessionalData professional = professionalService.getAllProfessionalsCompleteData(id);
        return new ResponseEntity<>(professional, HttpStatus.OK);
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
    public ResponseEntity<Professional> deleteProfessional(@PathVariable String id) {
        //portfolioItemService.deletePortfolioItems(id);
        //categoryDescriptionService.deleteCategoryDescriptionsByProfessionalId(id);
        //reviewService.deleteReviewsForUser(id);
        //professionalsFeeService.deleteProfessionalFees(id);
        //ticketService.deleteTickets(id);
        //serviceService.deleteServicesFroProfessional(id);

        Professional existingProfessional = professionalService.deleteProfessional(id);
        return new ResponseEntity<>(existingProfessional, HttpStatus.OK);
    }

}