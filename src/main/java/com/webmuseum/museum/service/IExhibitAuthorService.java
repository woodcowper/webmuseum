package com.webmuseum.museum.service;

import java.util.List;
import java.util.Optional;

import com.webmuseum.museum.dto.ExhibitAuthorDto;
import com.webmuseum.museum.entity.ExhibitAuthor;

public interface IExhibitAuthorService {
    List<ExhibitAuthor> findAllForAuthor(long id);

    List<ExhibitAuthor> findAllForExhibit(long id);

    List<ExhibitAuthorDto> findAllDtosForAuthor(long id);

    List<ExhibitAuthorDto> findAllDtosForExhibit(long id);

    List<ExhibitAuthor> mapToExhibitAuthorsList(List<ExhibitAuthorDto> exhibitAuthorDtos);

    Optional<ExhibitAuthor> getExhibitAuthorById(long exhibitId, long authorId);

    ExhibitAuthorDto getExhibitAuthorDtoById(long exhibitId, long authorId);

    void deleteExhibitAuthor(long exhibitId, long authorId);

    void saveExhibitAuthor(ExhibitAuthor exhibitAuthor);

    void saveExhibitAuthor(ExhibitAuthorDto exhibitAuthor);

    boolean checkIfExists(Long exhibitId, Long authorId);
    
}
