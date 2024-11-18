package com.example.fix4you_api.Service.Language;


import com.example.fix4you_api.Data.Models.Language;

import java.util.List;
import java.util.Map;

public interface LanguageService {
    List<Language> getAllLanguages();
    Language createLanguage(Language language);
    Language updateLanguage(String id, Language language);
    Language partialUpdateLanguage(String id, Map<String, Object> updates);
    void deleteLanguage(String id);
    boolean nameExists(String name);
}