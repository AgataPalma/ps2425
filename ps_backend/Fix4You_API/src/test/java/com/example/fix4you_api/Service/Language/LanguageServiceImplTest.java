package com.example.fix4you_api.Service.Language;

import com.example.fix4you_api.Data.Models.Language;
import com.example.fix4you_api.Data.MongoRepositories.LanguageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LanguageServiceImplTest {

    @Mock
    private LanguageRepository languageRepository;

    @InjectMocks
    private LanguageServiceImpl languageService;

    private Language language;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        language = new Language();
        language.setId("1");
        language.setName("English");
    }

    @Test
    void testGetAllLanguages() {
        when(languageRepository.findAll()).thenReturn(List.of(language));

        List<Language> languages = languageService.getAllLanguages();

        assertNotNull(languages);
        assertFalse(languages.isEmpty());
        assertEquals(1, languages.size());
        assertEquals("English", languages.get(0).getName());
        verify(languageRepository, times(1)).findAll();
    }

    @Test
    void testCreateLanguage() {
        when(languageRepository.save(any(Language.class))).thenReturn(language);

        Language createdLanguage = languageService.createLanguage(language);

        assertNotNull(createdLanguage);
        assertEquals("English", createdLanguage.getName());
        verify(languageRepository, times(1)).save(language);
    }

    @Test
    void testUpdateLanguage() {
        Language updatedLanguage = new Language();
        updatedLanguage.setName("French");

        when(languageRepository.findById("1")).thenReturn(Optional.of(language));
        when(languageRepository.save(any(Language.class))).thenReturn(language);

        Language result = languageService.updateLanguage("1", updatedLanguage);

        assertEquals("French", result.getName());
        verify(languageRepository, times(1)).save(language);
    }

    @Test
    void testPartialUpdateLanguage() {
        Map<String, Object> updates = Map.of("name", "Spanish");

        when(languageRepository.findById("1")).thenReturn(Optional.of(language));
        when(languageRepository.save(any(Language.class))).thenReturn(language);

        Language result = languageService.partialUpdateLanguage("1", updates);

        assertEquals("Spanish", result.getName());
        verify(languageRepository, times(1)).save(language);
    }

    @Test
    void testPartialUpdateLanguage_InvalidField() {
        Map<String, Object> updates = Map.of("invalidField", "Some Value");

        when(languageRepository.findById("1")).thenReturn(Optional.of(language));

        assertThrows(RuntimeException.class, () -> languageService.partialUpdateLanguage("1", updates));
    }

    @Test
    void testDeleteLanguage() {
        when(languageRepository.findById("1")).thenReturn(Optional.of(language));

        languageService.deleteLanguage("1");

        verify(languageRepository, times(1)).deleteById("1");
    }

    @Test
    void testFindOrThrow_LanguageNotFound() {
        when(languageRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> languageService.updateLanguage("1", language));
    }

    @Test
    void testNameExists_True() {
        when(languageRepository.findAll()).thenReturn(List.of(language));

        boolean result = languageService.nameExists("English");

        assertTrue(result);
    }

    @Test
    void testNameExists_False() {
        when(languageRepository.findAll()).thenReturn(List.of(language));

        boolean result = languageService.nameExists("German");

        assertFalse(result);
    }
}
