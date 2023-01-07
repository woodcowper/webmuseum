package com.webmuseum.museum.service;

import java.util.List;
import java.util.Optional;

import com.webmuseum.museum.dto.ExhibitDto;
import com.webmuseum.museum.dto.ExhibitViewDto;
import com.webmuseum.museum.entity.Exhibit;

public interface IExhibitService {
    List<ExhibitDto> findAllExhibits();

    List<ExhibitDto> findAllExhibitsForAuthor(long id);

    Optional<Exhibit> getExhibitById(long id);

    ExhibitDto getExhibitDtoById(long id);

    void deleteExhibit(long id);

    void saveExhibit(Exhibit exhibit);

    void saveExhibit(ExhibitDto exhibit);

    boolean checkIfExistsOthers(Long exhibitId, String name, Long authorId);

    ExhibitViewDto getExhibitViewDto(Long exhibitId);

    ExhibitViewDto getExhibitViewDto(Long exhibitId, Long languageId);

}
