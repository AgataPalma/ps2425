package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Models.Client;
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

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> createClient(@RequestBody Client client) throws IOException {

        // check if email already exists
        if(userService.emailExists(client.getEmail())){
            return new ResponseEntity<>("O email já existe!", HttpStatus.CONFLICT);
        }

        Client createdClient = clientService.createClient(client);
        // send verification email
        userService.sendValidationEmailUserRegistration(createdClient.getEmail());
        return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients(@RequestParam(value = "includeSuspended", required = false, defaultValue = "false") boolean includeSuspended) {
        List<Client> clients = clientService.getAllClients();

        // remove suspended clients
        if (!includeSuspended) {
            for (var i = 0; i < clients.size(); i++) {
                if (clients.get(i).isSuspended()) {
                    clients.remove(clients.get(i));
                }
            }
        }

        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable String id) {
        Client client = clientService.getClientById(id);
        return new ResponseEntity<>(client, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable String id,
                                               @RequestBody Client client) throws IOException {
        Client updatedClient = clientService.updateClient(id, client);
        return new ResponseEntity<>(updatedClient, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Client> partialUpdateClient(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        Client updatedClient = clientService.partialUpdateClient(id, updates);
        return new ResponseEntity<>(updatedClient, HttpStatus.OK);
    }

    @PutMapping("/image/{id}")
    public ResponseEntity<Client> updateClientImage(@PathVariable String id,
                                                    @Validated @RequestParam("profileImage") byte[] profileImage) throws IOException {

        Client updatedClient = clientService.updateClientImage(id, profileImage);
        return new ResponseEntity<>(updatedClient, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable String id) {

        if (serviceService.checkServicesToDeleteClient(id)) {

            reviewService.deleteReviewsForUser(id);
            ticketService.deleteTicketsForUser(id);
            serviceService.deleteServicesForClient(id);
            clientService.deleteClient(id);

            return new ResponseEntity<>("Cliente removido com sucesso.", HttpStatus.OK);

        } else {
            return new ResponseEntity<>(
                    "Não é possível remover o cliente porque existem serviços pendentes.",
                    HttpStatus.BAD_REQUEST
            );
        }

        //Client existingClient = clientService.deleteClient(id);
        //return new ResponseEntity<>(existingClient ,HttpStatus.OK);
    }
}
