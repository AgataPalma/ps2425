package com.example.fix4you_api.Service.Client;

import com.example.fix4you_api.Data.Models.Client;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ClientService {

    Client createClient(Client client) throws IOException;

    Client getClientById(String id);

    List<Client> getAllClients();

    Client updateClient(String id, Client client) throws IOException;

    Client partialUpdateClient(String id, Map<String, Object> updates);

    Client updateClientImage(String id, byte[] profileImage);

    void deleteClient(String id);

    void setRating(float rating, Client client);

    Client getClientByIdNotThrow(String id);
}
