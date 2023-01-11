package com.webmuseum.museum.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webmuseum.museum.dto.AuthorDto;
import com.webmuseum.museum.entity.Author;
import com.webmuseum.museum.entity.AuthorDescription;
import com.webmuseum.museum.repository.AuthorRepository;
import com.webmuseum.museum.service.IAuthorService;
import com.webmuseum.museum.service.ILanguageService;
import com.webmuseum.museum.utils.DateHelper;
import com.webmuseum.museum.utils.LanguageHelper;


@Service
public class AuthorServiceImpl implements IAuthorService{

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ILanguageService languageService;

    @Override
    public List<AuthorDto> findAllAuthors() {
        return findAllAuthors(LanguageHelper.DEFAULS_LANGUAGE_ID);
    }

    @Override
    public List<AuthorDto> findAllAuthors(long languageId) {
        List<Author> authors = authorRepository.findAll();
        return authors.stream()
                .map((author) -> mapToAuthorDto(author, languageId))
                .sorted((author1, author2) -> author1.getFullName().compareTo(author2.getFullName()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Author> getAuthorById(long id) {
        return authorRepository.findById(id);
    }

    @Override
    public AuthorDto getAuthorDtoById(long id, long languageId) {
        return mapToAuthorDto(getAuthorById(id).get(), languageId);
    }

    @Override
    public void deleteAuthor(long id) {
        Optional<Author> author = authorRepository.findById(id);
        if (author.isPresent()) {
            authorRepository.delete(author.get());
        }
        
    }

    @Override
    public void saveAuthor(Author author) {
        authorRepository.save(author);
    }

    @Override
    public void saveAuthor(AuthorDto author) {
        authorRepository.save(mapToAuthor(author));
        
    }

    private AuthorDto mapToAuthorDto(Author author, long languageId){
        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(author.getId());
        authorDto.setFullName(getName(author, languageId));
        authorDto.setDescription(getDesc(author, languageId));
        authorDto.setBirthDate(DateHelper.parseDateToStr(author.getBirthDate()));
        authorDto.setDieDate(DateHelper.parseDateToStr(author.getDieDate()));
        authorDto.setLanguageId(languageId);
        return authorDto;
    }

    private Author mapToAuthor(AuthorDto authorDto){
        Author author;
        if(authorDto.getId() == null){
            author = new Author();
        } else {
            author = getAuthorById(authorDto.getId()).get();
        }
        author.setBirthDate(DateHelper.parseStrToDate(authorDto.getBirthDate()));
        author.setDieDate(DateHelper.parseStrToDate(authorDto.getDieDate()));

        AuthorDescription authorDescription = getDescription(author, authorDto.getLanguageId());
        if(authorDescription == null){
            authorDescription = new AuthorDescription();
            authorDescription.setLanguage(languageService.getLanguageById(authorDto.getLanguageId()).get());
            authorDescription.setAuthor(author);
            author.getDescriptions().add(authorDescription);
        }
        authorDescription.setName(authorDto.getFullName());
        authorDescription.setDescription(authorDto.getDescription());
        return author;
    }
    
    @Override
    public AuthorDescription getDescription(Author author, long languageId) {
        Optional<AuthorDescription> description = author.getDescriptions().stream()
                .filter((desc) -> desc.getLanguage().getId() == languageId)
                .findFirst();
        if (description.isPresent()) {
            return description.get();
        }
        return null;
    }

    private String getName(Author author, long languageId){
        AuthorDescription authorDescription =  getDescription(author, languageId);
        if(authorDescription == null){
            return "";
        }
        return authorDescription.getName();
    }

    private String getDesc(Author author, long languageId){
        AuthorDescription authorDescription =  getDescription(author, languageId);
        if(authorDescription == null){
            return "";
        }
        return authorDescription.getDescription();
    }

}
