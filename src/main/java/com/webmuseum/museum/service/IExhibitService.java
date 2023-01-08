package com.webmuseum.museum.service;

import java.util.List;
import java.util.Optional;

import com.webmuseum.museum.dto.ExhibitDto;
import com.webmuseum.museum.dto.ExhibitViewDto;
import com.webmuseum.museum.entity.Exhibit;
import com.webmuseum.museum.entity.ExhibitDescription;

public interface IExhibitService {
    List<ExhibitDto> findAllExhibits();

    List<ExhibitDto> findAllExhibits(long languageId);

    List<ExhibitDto> findAllExhibitsForAuthor(long id);

    List<ExhibitDto> findAllExhibitsForAuthor(long id, long languageId);

    Optional<Exhibit> getExhibitById(long id);

    ExhibitDto getExhibitDtoById(long id, long languageId);

    void deleteExhibit(long id);

    void saveExhibit(Exhibit exhibit);

    void saveExhibit(ExhibitDto exhibit);

    boolean checkIfExistsOthers(Long exhibitId, String name, Long authorId, long languageId);

    ExhibitViewDto getExhibitViewDto(Long exhibitId);

    ExhibitViewDto getExhibitViewDto(Long exhibitId, Long languageId);

    ExhibitDescription getDescription(Exhibit exhibit, long languageId);

}
