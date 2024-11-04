package com.example.fix4you_api.Service.Client;

import com.example.fix4you_api.Data.Models.Client;
import com.example.fix4you_api.Data.MongoRepositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    public Client createClient(Client client) {
        client.setIsEmailConfirmed(true);
        return clientRepository.save(client);
    }

    @Override
    public Client getClientById(String id) {
        return findOrThrow(id);
    }

    @Override
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @Override
    @Transactional
    public Client updateClient(String id, Client client) {
        Client existingClient = findOrThrow(id);
        BeanUtils.copyProperties(client, existingClient, "id");
        return clientRepository.save(existingClient);
    }

    @Override
    public void deleteClient(String id) {
        clientRepository.deleteById(id);
    }

    private Client findOrThrow(String id) {
        return clientRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException(String.format("Client %s not found", id)));
    }
}
