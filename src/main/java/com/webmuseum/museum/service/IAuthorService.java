package com.webmuseum.museum.service;

import java.util.List;
import java.util.Optional;

import com.webmuseum.museum.dto.AuthorDto;
import com.webmuseum.museum.entity.Author;
import com.webmuseum.museum.entity.AuthorDescription;

public interface IAuthorService {
    List<AuthorDto> findAllAuthors();

    List<AuthorDto> findAllAuthors(long languageId);

    Optional<Author> getAuthorById(long id);

    AuthorDto getAuthorDtoById(long id, long languageId);

    void deleteAuthor(long id);

    void saveAuthor(Author author);

    void saveAuthor(AuthorDto author);

    AuthorDescription getDescription(Author author, long languageId);
}