package com.example.fix4you_api.Service.Language;

import com.example.fix4you_api.Data.Models.Language;
import com.example.fix4you_api.Data.MongoRepositories.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;

    @Override
    public List<Language> getAllLanguages() {
        return languageRepository.findAll();
    }

    @Override
    public Language createLanguage(Language language) {
        return languageRepository.save(language);
    }

    @Override
    @Transactional
    public Language updateLanguage(String id, Language language) {
        Language existingLanguage = findOrThrow(id);
        BeanUtils.copyProperties(language, existingLanguage, "id");
        return languageRepository.save(existingLanguage);
    }

    @Override
    @Transactional
    public Language partialUpdateLanguage(String id, Map<String, Object> updates) {
        Language existingLanguage = findOrThrow(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> existingLanguage.setName((String) value);
                default -> throw new RuntimeException("Campo inválido no pedido da atualização!");
            }
        });

        return languageRepository.save(existingLanguage);
    }

    @Override
    @Transactional
    public void deleteLanguage(String id) {
        languageRepository.deleteById(id);
    }

    private Language findOrThrow(String id) {
        return languageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Linguagem %s não encontrada!", id)));
    }

    @Override
    public boolean nameExists(String name) {
        List<Language> languages = languageRepository.findAll();

        for (Language language : languages) {
            if (language.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

}