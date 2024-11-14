package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Enums.LanguageEnum;
import com.example.fix4you_api.Data.Enums.PaymentTypesEnum;
import com.example.fix4you_api.Data.Models.Professional;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public ResponseEntity<?> createProfessional(@RequestParam String name,
                                                @RequestParam String phoneNumber,
                                                @RequestParam String location,
                                                @RequestParam Boolean ageValidation,
                                                @RequestParam EnumUserType userType,
                                                @RequestParam String password,
                                                @RequestParam String email,
                                                @RequestParam String description,
                                                @RequestParam String nif,
                                                @RequestParam List<LanguageEnum> languages,
                                                @RequestParam Integer locationsRange,
                                                @RequestParam List<PaymentTypesEnum> acceptedPayments,
                                                @RequestParam("file") MultipartFile file) throws IOException {
        // verify if the email and nif are unique
        if(userService.emailExists(email)){
            return new ResponseEntity<>("Email already exists", HttpStatus.CONFLICT);
        }

        if(professionalService.nifExists(nif)) {
            return new ResponseEntity<>("NIF already exists", HttpStatus.CONFLICT);
        }

        Professional professional = new Professional(description, nif,languages,locationsRange,acceptedPayments, 0);
        professional.setName(name);
        professional.setPhoneNumber(phoneNumber);
        professional.setLocation(location);
        professional.setAgeValidation(ageValidation);
        professional.setUserType(userType);
        professional.setPassword(password);
        professional.setEmail(email);

        if(!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            String contentType = file.getContentType();
            byte[] bytes = file.getBytes();

            professional.setFilename(fileName);
            professional.setContentType(contentType);
            professional.setFileData(bytes);
        }

        Professional createdProfessional = professionalService.createProfessional(professional);

        // send verification email
        //userService.sendValidationEmailUserRegistration(createdProfessioanl.getEmail());
        return new ResponseEntity<>(createdProfessional, HttpStatus.CREATED);
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
    public ResponseEntity<Professional> updateProfessional(@PathVariable String id,
                                                           @RequestParam String name,
                                                           @RequestParam String phoneNumber,
                                                           @RequestParam String location,
                                                           @RequestParam Boolean ageValidation,
                                                           @RequestParam EnumUserType userType,
                                                           @RequestParam String password,
                                                           @RequestParam String email,
                                                           @RequestParam String description,
                                                           @RequestParam String nif,
                                                           @RequestParam List<LanguageEnum> languages,
                                                           @RequestParam Integer locationsRange,
                                                           @RequestParam Boolean IsEmailConfirmed,
                                                           @RequestParam List<PaymentTypesEnum> acceptedPayments,
                                                           @RequestParam("file") MultipartFile file) throws IOException {
        Professional professional = professionalService.getProfessionalById(id);
        professional.setEmail(email);
        professional.setPassword(password);
        professional.setUserType(userType);
        professional.setName(name);
        professional.setPhoneNumber(phoneNumber);
        professional.setLanguages(languages);
        professional.setAgeValidation(ageValidation);
        professional.setDescription(description);
        professional.setNif(nif);
        professional.setLocation(location);
        professional.setLocationsRange(locationsRange);
        professional.setAcceptedPayments(acceptedPayments);
        professional.setIsEmailConfirmed(IsEmailConfirmed);

        if(!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            String contentType = file.getContentType();
            byte[] bytes = file.getBytes();

            professional.setFilename(fileName);
            professional.setContentType(contentType);
            professional.setFileData(bytes);
        } else {
            professional.setFilename("");
            professional.setContentType("");
            professional.setFileData(null);
        }

        Professional updatedProfessional = professionalService.updateProfessional(professional);
        return new ResponseEntity<>(updatedProfessional, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Professional> partialUpdateProfessional(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        Professional updatedProfessional = professionalService.partialUpdateProfessional(id, updates);
        return new ResponseEntity<>(updatedProfessional, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessional(@PathVariable String id) {
        portfolioItemService.deletePortfolioItems(id);
        categoryDescriptionService.deleteCategoryDescriptions(id);
        reviewService.deleteReviewsForUser(id);
        professionalsFeeService.deleteProfessionalFees(id);
        ticketService.deleteTickets(id);
        serviceService.deleteServicesFroProfessional(id);

        professionalService.deleteProfessional(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}