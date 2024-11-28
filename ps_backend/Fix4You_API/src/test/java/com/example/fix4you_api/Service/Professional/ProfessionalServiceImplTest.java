package com.example.fix4you_api.Service.Professional;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.MongoRepositories.ProfessionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ProfessionalServiceImplTest {

    @Mock
    private ProfessionalRepository professionalRepository;

    @InjectMocks
    private ProfessionalServiceImpl professionalService;

    private Professional professional;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        professional = new Professional();
        professional.setId("1");
        professional.setUserType(EnumUserType.PROFESSIONAL);
        professional.setEmail("test@example.com");
        professional.setName("Test Professional");
    }

    @Test
    public void testCreateProfessional() {
        when(professionalRepository.save(any(Professional.class))).thenReturn(professional);

        Professional createdProfessional = professionalService.createProfessional(professional);

        assertNotNull(createdProfessional);
        assertEquals("1", createdProfessional.getId());
    }

    @Test
    public void testGetProfessionalById() {
        when(professionalRepository.findById(anyString())).thenReturn(java.util.Optional.of(professional));

        Professional fetchedProfessional = professionalService.getProfessionalById("1");

        assertNotNull(fetchedProfessional);
        assertEquals("1", fetchedProfessional.getId());
    }

    @Test
    public void testDeleteProfessional() {
        doNothing().when(professionalRepository).deleteById(anyString());

        professionalService.deleteProfessional("1");

        verify(professionalRepository, times(1)).deleteById("1");
    }

    @Test
    public void testSetRating() {
        professional.setRating(4.5f);
        when(professionalRepository.save(any(Professional.class))).thenReturn(professional);

        professionalService.setRating(4.5f, professional);

        assertEquals(4.5f, professional.getRating());
    }
}
