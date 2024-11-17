package com.example.fix4you_api.Service.Client;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Models.Client;
import com.example.fix4you_api.Data.Models.Image;
import com.example.fix4you_api.Data.MongoRepositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    public Client createClient(Client client, MultipartFile file) throws IOException {
        client.setDateCreation(LocalDateTime.now());
        client.setIsEmailConfirmed(true);
        client.setRating(0);

        if(!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            String contentType = file.getContentType();
            byte[] bytes = file.getBytes();

            Image image = new Image();
            image.setFilename(fileName);
            image.setContentType(contentType);
            image.setBytes(bytes);

            client.setImage(image);
        }

        return clientRepository.save(client);
    }

    @Override
    public Client getClientById(String id) {
        return findOrThrow(id);
    }

    @Override
    public List<Client> getAllClients() {
        return clientRepository.findByUserType(EnumUserType.CLIENT);
    }

    @Override
    @Transactional
    public Client updateClient(String id, Client client, MultipartFile file) throws IOException {
        Client existingClient = findOrThrow(id);

        BeanUtils.copyProperties(client, existingClient, "id","rating");

        if(!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            String contentType = file.getContentType();
            byte[] bytes = file.getBytes();

            Image image = new Image();
            image.setFilename(fileName);
            image.setContentType(contentType);
            image.setBytes(bytes);

            client.setImage(image);
        } else {
            client.setImage(null);
        }

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
                case "userType" -> client.setUserType((EnumUserType) value);
                case "password" -> client.setPassword((String) value);
                case "location" -> client.setLocation((String) value);
                case "rating" -> client.setRating((float) value);
                case "IsDeleted" -> client.setIsDeleted((Boolean) value);
                case "IsEmailConfirmed" -> client.setIsEmailConfirmed((Boolean) value);
                default -> throw new RuntimeException("Invalid field update request");
            }
        });

        return clientRepository.save(client);
    }

    @Override
    public Client deleteClient(String id) {
        Client existingClient = findOrThrow(id);
        existingClient.setIsDeleted(true);
        return clientRepository.save(existingClient);

        //clientRepository.deleteById(id);
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
                        String.format("Client %s not found or user is not a client", id)));
    }

}
