package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Models.*;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> createProfessional(@RequestBody Professional professional) throws IOException {
        // verify if the email and nif are unique
        if(userService.emailExists(professional.getEmail())){
            return new ResponseEntity<>("O email já existe!", HttpStatus.CONFLICT);
        }

        if(professionalService.nifExists(professional.getNif())) {
            return new ResponseEntity<>("O NIF já existe!", HttpStatus.CONFLICT);
        }

        Professional createdProfessional = professionalService.createProfessional(professional);

        // send verification email
        //userService.sendValidationEmailUserRegistration(createdProfessioanl.getEmail());
        return new ResponseEntity<>(createdProfessional, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Professional>> getProfessionals(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "sort", required = false) String sort,
    @RequestParam(value = "includeSuspended", required = false, defaultValue = "false") boolean includeSuspended){
        List<Professional> professionals = professionalService.getProfessionals(filter, sort);

        // remove suspended professionals
        if (!includeSuspended) {
            for (var i = 0; i < professionals.size(); i++) {
                if (professionals.get(i).isSuspended()) {
                    professionals.remove(professionals.get(i));
                }
            }
        }

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

    @GetMapping("/professional-categories/{id}")
    public ResponseEntity<List<String>> getProfessionalCategories(@PathVariable("id") String id) {

        return new ResponseEntity<>(professionalService.getProfessionalCategories(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Professional> updateProfessional(@PathVariable String id,
                                                           @RequestBody Professional professional) throws IOException {

        Professional updatedProfessional = professionalService.updateProfessional(id, professional);
        return new ResponseEntity<>(updatedProfessional, HttpStatus.OK);
    }

    @PutMapping("/image/{id}")
    public ResponseEntity<Client> updateProfessionalImage(@PathVariable String id,
                                                    @Validated @RequestParam("profileImage") byte[] profileImage) throws IOException {

        Professional updatedProfessional = professionalService.updateProfessionalImage(id, profileImage);
        return new ResponseEntity<>(updatedProfessional, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Professional> partialUpdateProfessional(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        Professional updatedProfessional = professionalService.partialUpdateProfessional(id, updates);
        return new ResponseEntity<>(updatedProfessional, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProfessional(@PathVariable String id) {

            if(serviceService.checkServicesToDeleteProfessional(id)) {

                portfolioItemService.deletePortfolioItemsForProfessional(id);
                categoryDescriptionService.deleteCategoryDescriptionsForProfessional(id);
                reviewService.deleteReviewsForUser(id);
                //professionalsFeeService.deleteProfessionalFeesForProfessional(id);
                ticketService.deleteTicketsForUser(id);
                serviceService.deleteServicesForProfessional(id);
                professionalService.deleteProfessional(id);

                return new ResponseEntity<>("Profissional removido com sucesso.", HttpStatus.OK);

            } else {
                return new ResponseEntity<>(
                        "Não é possível remover o profissional porque existem serviços pendentes.",
                        HttpStatus.BAD_REQUEST
                );
            }

        //Professional existingProfessional = professionalService.deleteProfessional(id);
        //return new ResponseEntity<>(existingProfessional, HttpStatus.OK);
    }

}