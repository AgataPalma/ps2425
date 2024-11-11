package com.example.fix4you_api.Service.Client;

import com.example.fix4you_api.Data.Models.Client;

import java.util.List;

public interface ClientService {

    Client createClient(Client client);

    Client getClientById(String id);

    List<Client> getAllClients();

    Client updateClient(String id, Client client);

    void deleteClient(String id);

    void setRating(float rating, Client client);
}
