package com.webmuseum.museum.service.impl;

import java.io.Console;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.text.DateFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webmuseum.museum.dto.AuthorDto;
import com.webmuseum.museum.entity.Author;
import com.webmuseum.museum.repository.AuthorRepository;
import com.webmuseum.museum.service.IAuthorService;
import com.webmuseum.museum.utils.DateHelper;


@Service
public class AuthorServiceImpl implements IAuthorService{

    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public List<AuthorDto> findAllAuthors() {
        List<Author> authors = authorRepository.findAll();
        return authors.stream()
                .sorted((author1, author2) -> author1.getName().compareTo(author2.getName()))
                .map((author) -> mapToAuthorDto(author))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Author> getAuthorById(long id) {
        return authorRepository.findById(id);
    }

    @Override
    public AuthorDto getAuthorDtoById(long id) {
        return mapToAuthorDto(authorRepository.findById(id).get());
    }

    @Override
    public void addAuthor(String name, String description, Date birthDate, Date dieDate) {
        authorRepository.save(new Author(name, description, birthDate, dieDate));
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

    private AuthorDto mapToAuthorDto(Author author){
        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(author.getId());
        authorDto.setFullName(author.getName());
        authorDto.setDescription(author.getDescription());
        authorDto.setBirthDate(DateHelper.parseDateToStr(author.getBirthDate()));
        authorDto.setDieDate(DateHelper.parseDateToStr(author.getDieDate()));
        return authorDto;
    }

    private Author mapToAuthor(AuthorDto authorDto){
        Author author;
        if(authorDto.getId() == null){
            author = new Author();
        } else {
            author = getAuthorById(authorDto.getId()).get();
        }
        author.setName(authorDto.getFullName());
        author.setDescription(authorDto.getDescription());
        author.setBirthDate(DateHelper.parseStrToDate(authorDto.getBirthDate()));
        author.setDieDate(DateHelper.parseStrToDate(authorDto.getDieDate()));
        return author;
    }
    
}
