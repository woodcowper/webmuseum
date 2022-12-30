package com.webmuseum.museum.service.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webmuseum.museum.dto.ExhibitDto;
import com.webmuseum.museum.entity.Exhibit;
import com.webmuseum.museum.entity.ExhibitAuthor;
import com.webmuseum.museum.repository.ExhibitRepository;
import com.webmuseum.museum.service.ICategoryService;
import com.webmuseum.museum.service.IExhibitAuthorService;
import com.webmuseum.museum.service.IExhibitService;
import com.webmuseum.museum.service.IStorageService;
import com.webmuseum.museum.utils.ResourceHelper;

@Service
public class ExhibitServiceImpl implements IExhibitService {

    @Autowired
    private ExhibitRepository exhibitRepository;

    @Autowired 
    @Lazy
    private IExhibitAuthorService exhibitAuthorService;

    @Autowired 
    private ICategoryService categoryService;


    @Autowired
    private IStorageService storageService;

    @Override
    public List<ExhibitDto> findAllExhibits(){
        return exhibitRepository.findAll().stream()
            .sorted((exhibit1, exhibit2) -> exhibit1.getName().compareTo(exhibit2.getName()))
            .map((exhibit) -> mapToExhibitDto(exhibit))
            .collect(Collectors.toList());
    }

    @Override
    public List<ExhibitDto> findAllExhibitsForAuthor(long id) {
        return exhibitAuthorService.findAllForAuthor(id).stream()
            .map((exhibitAuthor) -> mapToExhibitDto(exhibitAuthor.getExhibit()))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Exhibit> getExhibitById(long id) {
        return exhibitRepository.findById(id);
    }

    @Override
    public ExhibitDto getExhibitDtoById(long id) {
        return mapToExhibitDto(getExhibitById(id).get());
    }

    @Override
    public void deleteExhibit(long id) {
        Optional<Exhibit> exhibit = getExhibitById(id);
        if (exhibit.isPresent()) {
            exhibitRepository.delete(exhibit.get());
        }
    }

    @Override
    public void saveExhibit(Exhibit exhibit) {
        if(exhibit.getId() == null && exhibit.getAuthors().size() > 0){
            List<ExhibitAuthor> authors = new ArrayList<ExhibitAuthor>(exhibit.getAuthors());
            exhibit.getAuthors().clear();
            exhibitRepository.save(exhibit);
            authors.stream().forEach(el -> el.setExhibit(exhibit));
            exhibit.getAuthors().addAll(authors);
        }
        exhibitRepository.save(exhibit);
    }

    @Override
    public void saveExhibit(ExhibitDto exhibit) {
        if(exhibit.getImage() != null && !exhibit.getImage().isEmpty()){
            String fileName = storageService.store(exhibit.getImage(), "");
            exhibit.setImgFileName(fileName);
        } else if(exhibit.getImgFileName().isEmpty()) {
            if(exhibit.getId() != null){
                Exhibit oldExhibit = getExhibitById(exhibit.getId()).get();
                if(oldExhibit.getImgFileName() != null && !oldExhibit.getImgFileName().equals(exhibit.getImgFileName())){
                    storageService.delete(oldExhibit.getImgFileName());
                }
            }
            exhibit.setImgFileName(null);
        }
        saveExhibit(mapToExhibit(exhibit));
    }

    @Override
    public boolean checkIfExistsOthers(Long exhibitId, String name, Long authorId) {
        return findAllExhibitsForAuthor(authorId).stream()
                .filter((exhibit) -> exhibit.getName().equals(name)
                                        && exhibit.getId() != exhibitId)
                .findAny()
                .isPresent();
    }

    private ExhibitDto mapToExhibitDto(Exhibit exhibit){
        ExhibitDto exhibitDto = new ExhibitDto();
        exhibitDto.setId(exhibit.getId());
        exhibitDto.setName(exhibit.getName());
        exhibitDto.setDescription(exhibit.getDescription());
        exhibitDto.setImgFileName(exhibit.getImgFileName());

        List<Long> categories = exhibit.getCategories().stream()
                                .map((category) -> category.getId())
                                .collect(Collectors.toList());
        exhibitDto.setCategories(categories);

        exhibitDto.setAuthors(exhibitAuthorService.findAllDtosForExhibit(exhibit.getId()));
        if(exhibit.getImgFileName() != null){
            exhibitDto.setImgUrl(ResourceHelper.getImgUrl(exhibit.getImgFileName()));
        }
        return exhibitDto;
    }

    private Exhibit mapToExhibit(ExhibitDto exhibitDto){
        Exhibit exhibit;
        if(exhibitDto.getId() == null){
            exhibit = new Exhibit();
        } else {
            exhibit = getExhibitById(exhibitDto.getId()).get();
        }
        exhibit.setName(exhibitDto.getName());
        exhibit.setDescription(exhibitDto.getDescription());
        exhibit.setImgFileName(exhibitDto.getImgFileName());

        exhibit.getCategories().clear();
        exhibit.getCategories().addAll(categoryService.findAllExhibitCategoriesWithIds(exhibitDto.getCategories()));
    
        exhibit.getAuthors().clear();
        exhibit.getAuthors().addAll(exhibitAuthorService.mapToExhibitAuthorsList(exhibitDto.getAuthors()));
        return exhibit;
    }
    
}