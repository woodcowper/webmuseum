package com.webmuseum.museum.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webmuseum.museum.dto.LanguageDto;
import com.webmuseum.museum.entity.Author;
import com.webmuseum.museum.entity.Language;
import com.webmuseum.museum.repository.LanguageRepository;
import com.webmuseum.museum.service.ILanguageService;

@Service
public class LanguageServiceImpl implements ILanguageService{

    @Autowired
    private LanguageRepository languageRepository;

    @Override
    public List<LanguageDto> findAllLanguages() {
        List<Language> languages = languageRepository.findAll();
        return languages.stream()
                .sorted((language1, language2) -> language1.getName().compareTo(language2.getName()))
                .map((language) -> mapToLanguageDto(language))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Language> getLanguageById(long id) {
        return languageRepository.findById(id);
    }

    @Override
    public LanguageDto getLanguageDtoById(long id) {
        return mapToLanguageDto(languageRepository.findById(id).get());
    }

    @Override
    public void deleteLanguage(long id) {
        Optional<Language> language = languageRepository.findById(id);
        if (language.isPresent()) {
            languageRepository.delete(language.get());
        }
    }

    @Override
    public void saveLanguage(Language language) {
        languageRepository.save(language);
    }

    @Override
    public void saveLanguage(LanguageDto language) {
        languageRepository.save(mapToLanguage(language));
    }

    @Override
    public boolean checkIfExistsOthers(Long languageId, String name) {
        Optional<Language> optLanguage = getLanguageById(languageId);
        if(optLanguage.isPresent()){
            return optLanguage.get().getId() != languageId && optLanguage.get().getName().equals(name);
        }
        return false;
    }

    private LanguageDto mapToLanguageDto(Language language){
        LanguageDto languageDto = new LanguageDto();
        languageDto.setId(language.getId());
        languageDto.setName(language.getName());
        return languageDto;
    }

    private Language mapToLanguage(LanguageDto languageDto){
        Language language;
        if(languageDto.getId() == null){
            language = new Language();
        } else {
            language = getLanguageById(languageDto.getId()).get();
        }
        language.setName(languageDto.getName());

        return language;
    }

}
