package com.webmuseum.museum.service;

import java.util.List;
import java.util.Optional;

import com.webmuseum.museum.dto.LanguageDto;
import com.webmuseum.museum.entity.Language;

public interface ILanguageService {

    List<LanguageDto> findAllLanguages();

    Optional<Language> getLanguageById(long id);

    LanguageDto getLanguageDtoById(long id);

    void deleteLanguage(long id);

    void saveLanguage(Language language);

    void saveLanguage(LanguageDto language);

    boolean checkIfExistsOthers(Long languageId, String name);
    
}
