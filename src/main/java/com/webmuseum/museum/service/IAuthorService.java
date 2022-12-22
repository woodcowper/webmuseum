package com.webmuseum.museum.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.webmuseum.museum.dto.AuthorDto;
import com.webmuseum.museum.entity.Author;

public interface IAuthorService {
    List<AuthorDto> findAllAuthors();

    Optional<Author> getAuthorById(long id);

    AuthorDto getAuthorDtoById(long id);

    void addAuthor(String name, String description, Date birthDate, Date dieDate);

    void deleteAuthor(long id);

    void saveAuthor(Author author);

    void saveAuthor(AuthorDto author);
}