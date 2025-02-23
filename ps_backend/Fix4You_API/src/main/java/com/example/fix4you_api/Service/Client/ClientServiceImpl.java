package com.example.fix4you_api.Service.Client;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Models.Client;
import com.example.fix4you_api.Data.MongoRepositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    public Client createClient(Client client) {
        client.setUserType(EnumUserType.CLIENT);
        client.setDateCreation(LocalDateTime.now());
        client.setIsEmailConfirmed(false);
        client.setRating(0);
        client.setSuspended(false);
        client.setSuspensionReason("");

        return clientRepository.save(client);
    }

    @Override
    public Client getClientById(String id) {
        return findOrThrow(id);
    }

    public Client getClientByIdNotThrow(String id) {
        Optional<Client> client = clientRepository.findById(id);
        return client.orElse(null);
    }

    @Override
    public List<Client> getAllClients() {
        return clientRepository.findByUserType(EnumUserType.CLIENT);
    }

    @Override
    @Transactional
    public Client updateClient(String id, Client client) {
        Client existingClient = findOrThrow(id);

        BeanUtils.copyProperties(client, existingClient, "id","rating");

        return clientRepository.save(existingClient);
    }

    @Override
    @Transactional
    public Client partialUpdateClient(String id, Map<String, Object> updates) {
        Client client = findOrThrow(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> client.setName((String) value);
                case "phoneNumber" -> client.setPhoneNumber((String) value);
                case "ageValidation" -> client.setAgeValidation((Boolean) value);
                case "email" -> client.setEmail((String) value);
                case "userType" -> {
                    try {
                        client.setUserType(EnumUserType.valueOf(value.toString().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Valor inválido para o estado: " + value);
                    }
                }
                case "password" -> client.setPassword((String) value);
                case "location" -> client.setLocation((String) value);
                case "rating" -> client.setRating((float) value);
                //case "IsDeleted" -> client.setIsDeleted((Boolean) value);
                case "IsEmailConfirmed" -> client.setIsEmailConfirmed((Boolean) value);
                case "isSuspended" -> client.setSuspended((boolean) value);
                case "suspensionReason" -> client.setSuspensionReason((String) value);
                default -> throw new RuntimeException("Campo inválido no pedido da atualização!");
            }
        });

        return clientRepository.save(client);
    }

    @Override
    @Transactional
    public Client updateClientImage(String id, byte[] profileImage){
        Client client = findOrThrow(id);
        client.setProfileImage(profileImage);

        return clientRepository.save(client);
    }

    @Override
    public void deleteClient(String id) {
        //Client existingClient = findOrThrow(id);
        //existingClient.setIsDeleted(true);
        //return clientRepository.save(existingClient);

        clientRepository.deleteById(id);
    }

    @Override
    public void setRating(float rating, Client client){
        client.setRating(rating);
        clientRepository.save(client);
    }

    private Client findOrThrow(String id) {
        return clientRepository.findById(id)
                .filter(client -> client.getUserType() == EnumUserType.CLIENT)
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("Cliente %s não encontrado! Ou o utilizador não é um cliente!", id)));
    }

}