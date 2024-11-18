package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Models.Language;
import com.example.fix4you_api.Service.Language.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/languages")
@RequiredArgsConstructor
public class LanguageController {
    private final LanguageService languageService;

    @GetMapping
    public ResponseEntity<List<Language>> getAllLanguages() {
        List<Language> languages = languageService.getAllLanguages();
        return new ResponseEntity<>(languages, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createLanguage(@RequestBody Language language) {
        if(languageService.nameExists(language.getName())){
            return new ResponseEntity<>("Language already exists", HttpStatus.CONFLICT);
        }

        Language createdLanguage = languageService.createLanguage(language);
        return new ResponseEntity<>(createdLanguage, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Language> updateLanguage(@PathVariable String id, @RequestBody Language language) {
        Language updatedLanguage = languageService.updateLanguage(id, language);
        return new ResponseEntity<>(updatedLanguage, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Language> partialUpdateLanguage(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        Language updatedLanguage = languageService.partialUpdateLanguage(id, updates);
        return new ResponseEntity<>(updatedLanguage, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLanguage(@PathVariable String id) {
        languageService.deleteLanguage(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}