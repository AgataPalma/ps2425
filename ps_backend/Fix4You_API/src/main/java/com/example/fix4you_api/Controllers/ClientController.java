package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Models.Client;
import com.example.fix4you_api.Data.Models.PortfolioItem;
import com.example.fix4you_api.Service.Client.ClientService;
import com.example.fix4you_api.Service.Review.ReviewService;
import com.example.fix4you_api.Service.Service.ServiceService;
import com.example.fix4you_api.Service.Ticket.TicketService;
import com.example.fix4you_api.Service.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final UserService userService;
    private final ReviewService reviewService;
    private final TicketService ticketService;
    private final ServiceService serviceService;

    @PostMapping
    public ResponseEntity<?> createClient(@RequestParam String name,
                                          @RequestParam String phoneNumber,
                                          @RequestParam String location,
                                          @RequestParam Boolean ageValidation,
                                          @RequestParam EnumUserType userType,
                                          @RequestParam String password,
                                          @RequestParam String email,
                                          @Validated @RequestParam("file") MultipartFile file) throws IOException {

        // check if email already exists
        if(userService.emailExists(email)){
            return new ResponseEntity<>("Email already exists", HttpStatus.CONFLICT);
        }

        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        byte[] bytes = file.getBytes();

        Client client = new Client();
        client.setName(name);
        client.setPhoneNumber(phoneNumber);
        client.setLocation(location);
        client.setAgeValidation(ageValidation);
        client.setUserType(userType);
        client.setPassword(password);
        client.setFilename(fileName);
        client.setContentType(contentType);
        client.setFileData(bytes);
        client.setEmail(email);

        Client createdClient = clientService.createClient(client);
        // send verification email
        //userService.sendValidationEmailUserRegistration(createdClient.getEmail());
        return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable String id) {
        Client client = clientService.getClientById(id);
        return new ResponseEntity<>(client, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = clientService.getAllClients();
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable String id,
                                               @RequestParam String name,
                                               @RequestParam String phoneNumber,
                                               @RequestParam String location,
                                               @RequestParam Boolean ageValidation,
                                               @RequestParam EnumUserType userType,
                                               @RequestParam String password,
                                               @RequestParam String email,
                                               @Validated @RequestParam("file") MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        byte[] bytes = file.getBytes();

        Client client = clientService.getClientById(id);
        client.setName(name);
        client.setPhoneNumber(phoneNumber);
        client.setLocation(location);
        client.setAgeValidation(ageValidation);
        client.setUserType(userType);
        client.setPassword(password);
        client.setFilename(fileName);
        client.setContentType(contentType);
        client.setFileData(bytes);
        client.setEmail(email);

        Client updatedClient = clientService.updateClient(id, client);
        return new ResponseEntity<>(updatedClient, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable String id) {
        reviewService.deleteReviewsForUser(id);
        ticketService.deleteTickets(id);
        serviceService.deleteServicesForClient(id);

        clientService.deleteClient(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
