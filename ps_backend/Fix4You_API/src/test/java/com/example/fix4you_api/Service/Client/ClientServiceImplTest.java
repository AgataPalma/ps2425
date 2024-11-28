package com.example.fix4you_api.Service.Client;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Models.Client;
import com.example.fix4you_api.Data.MongoRepositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client client;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        client = new Client();
        client.setId("1");
        client.setName("John Doe");
        client.setEmail("john.doe@example.com");
        client.setUserType(EnumUserType.CLIENT);
        client.setDateCreation(LocalDateTime.now());
        client.setIsEmailConfirmed(true);
        client.setRating(0);
        client.setSupended(false);
    }

    @Test
    void testCreateClient() {
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        Client createdClient = clientService.createClient(client);

        assertNotNull(createdClient);
        assertEquals("John Doe", createdClient.getName());
        assertEquals(EnumUserType.CLIENT, createdClient.getUserType());
        assertEquals(0, createdClient.getRating());
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void testGetClientById() {
        when(clientRepository.findById("1")).thenReturn(Optional.of(client));

        Client foundClient = clientService.getClientById("1");

        assertNotNull(foundClient);
        assertEquals("John Doe", foundClient.getName());
        verify(clientRepository, times(1)).findById("1");
    }

    @Test
    void testGetClientById_ClientNotFound() {
        when(clientRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> clientService.getClientById("1"));
    }

    @Test
    void testGetAllClients() {
        when(clientRepository.findByUserType(EnumUserType.CLIENT)).thenReturn(List.of(client));

        var clients = clientService.getAllClients();

        assertNotNull(clients);
        assertFalse(clients.isEmpty());
        assertEquals(1, clients.size());
        verify(clientRepository, times(1)).findByUserType(EnumUserType.CLIENT);
    }

    @Test
    void testUpdateClient() {
        Client updatedClient = new Client();
        updatedClient.setName("Jane Doe");

        when(clientRepository.findById("1")).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        Client result = clientService.updateClient("1", updatedClient);

        assertEquals("Jane Doe", result.getName());
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void testPartialUpdateClient() {
        Client updatedClient = new Client();
        updatedClient.setName("Jane Doe");

        when(clientRepository.findById("1")).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        Map<String, Object> updates = Map.of("name", "Jane Doe");
        Client result = clientService.partialUpdateClient("1", updates);

        assertEquals("Jane Doe", result.getName());
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void testPartialUpdateClient_InvalidField() {
        Map<String, Object> updates = Map.of("invalidField", "Some Value");

        assertThrows(RuntimeException.class, () -> clientService.partialUpdateClient("1", updates));
    }

    @Test
    void testDeleteClient() {
        when(clientRepository.findById("1")).thenReturn(Optional.of(client));

        clientService.deleteClient("1");

        verify(clientRepository, times(1)).deleteById("1");
    }

    @Test
    void testSetRating() {
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        clientService.setRating(5.0f, client);

        assertEquals(5.0f, client.getRating());
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void testUpdateClientImage() {
        byte[] newImage = new byte[10];
        when(clientRepository.findById("1")).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        Client updatedClient = clientService.updateClientImage("1", newImage);

        assertArrayEquals(newImage, updatedClient.getProfileImage());
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void testFindOrThrow_ClientNotFound() {
        when(clientRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> clientService.getClientById("1"));
    }
}
