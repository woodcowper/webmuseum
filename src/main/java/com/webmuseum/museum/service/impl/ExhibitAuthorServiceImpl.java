package com.webmuseum.museum.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webmuseum.museum.dto.ExhibitAuthorDto;
import com.webmuseum.museum.entity.AuthorDescription;
import com.webmuseum.museum.entity.ExhibitAuthor;
import com.webmuseum.museum.entity.ExhibitAuthorId;
import com.webmuseum.museum.entity.ExhibitDescription;
import com.webmuseum.museum.repository.ExhibitAuthorRepository;
import com.webmuseum.museum.service.IAuthorService;
import com.webmuseum.museum.service.ICollectionService;
import com.webmuseum.museum.service.IExhibitAuthorService;
import com.webmuseum.museum.service.IExhibitService;
import com.webmuseum.museum.utils.LanguageHelper;

@Service
public class ExhibitAuthorServiceImpl implements IExhibitAuthorService{

    @Autowired 
    private ExhibitAuthorRepository exhibitAuthorRepository;

    @Autowired 
    private IExhibitService exhibitService;

    @Autowired 
    private IAuthorService authorService;

    @Autowired 
    private ICollectionService collectionService;

    @Override
    public List<ExhibitAuthor> findAllForAuthor(long id){
        List<ExhibitAuthor> exhibitAuthors = exhibitAuthorRepository.findAll();
        return exhibitAuthors.stream()
                .filter((exhibitAuthor) -> exhibitAuthor.getAuthor().getId() == id)
                .sorted((exhibitAuthor1, exhibitAuthor2) -> {
                    ExhibitDescription exhibitDescription1 = exhibitService.getDescription(exhibitAuthor1.getExhibit(), LanguageHelper.DEFAULS_LANGUAGE_ID); 
                    ExhibitDescription exhibitDescription2 = exhibitService.getDescription(exhibitAuthor2.getExhibit(), LanguageHelper.DEFAULS_LANGUAGE_ID); 
                    return exhibitDescription1.getName().compareTo(exhibitDescription2.getName());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ExhibitAuthor> findAllForExhibit(long id){
        List<ExhibitAuthor> exhibitAuthors = exhibitAuthorRepository.findAll();
        return exhibitAuthors.stream()
                .filter((exhibitAuthor) -> exhibitAuthor.getExhibit().getId() == id)
                .sorted((exhibitAuthor1, exhibitAuthor2) -> {
                    AuthorDescription authorDesc1 = authorService.getDescription(exhibitAuthor1.getAuthor(), LanguageHelper.DEFAULS_LANGUAGE_ID);
                    AuthorDescription authorDesc2 = authorService.getDescription(exhibitAuthor1.getAuthor(), LanguageHelper.DEFAULS_LANGUAGE_ID);
                    return authorDesc1.getName().compareTo(authorDesc2.getName());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ExhibitAuthorDto> findAllDtosForAuthor(long id) {
        return findAllForAuthor(id).stream()
                .map((exhibitAuthor) -> mapToExhibitAuthorDto(exhibitAuthor))
                .collect(Collectors.toList());
    }

    @Override
    public List<ExhibitAuthorDto> findAllDtosForExhibit(long id) {
        return findAllForExhibit(id).stream()
                .map((exhibitAuthor) -> mapToExhibitAuthorDto(exhibitAuthor))
                .collect(Collectors.toList());
    }

    @Override 
    public List<ExhibitAuthor> mapToExhibitAuthorsList(List<ExhibitAuthorDto> exhibitAuthorDtos){
        return exhibitAuthorDtos.stream()
                .map((exhibitAuthor) -> mapToExhibitAuthor(exhibitAuthor))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ExhibitAuthor> getExhibitAuthorById(long exhibitId, long authorId) {
        List<ExhibitAuthor> exhibitAuthors = exhibitAuthorRepository.findAll();
        return exhibitAuthors.stream()
                .filter((exhibitAuthor) -> exhibitAuthor.getExhibit().getId() == exhibitId 
                                        && exhibitAuthor.getAuthor().getId() == authorId)
                .findFirst();
    }

    @Override
    public ExhibitAuthorDto getExhibitAuthorDtoById(long exhibitId, long authorId) {
        return mapToExhibitAuthorDto(getExhibitAuthorById(exhibitId, authorId).get());
    }

    @Override
    public void deleteExhibitAuthor(long exhibitId, long authorId) {
        Optional<ExhibitAuthor> exhibitAuthor = getExhibitAuthorById(exhibitId, authorId);
        if (exhibitAuthor.isPresent()) {
            exhibitAuthorRepository.delete(exhibitAuthor.get());
        }
    }

    @Override
    public void saveExhibitAuthor(ExhibitAuthor exhibitAuthor) {
        exhibitAuthorRepository.save(exhibitAuthor);
        
    }

    @Override
    public void saveExhibitAuthor(ExhibitAuthorDto exhibitAuthor) {
        exhibitAuthorRepository.save(mapToExhibitAuthor(exhibitAuthor));
    }

    @Override
    public boolean checkIfExists(Long exhibitId, Long authorId) {
        if(exhibitId == null || authorId == null){
            return false;
        }
        return getExhibitAuthorById(exhibitId, authorId).isPresent();
    }

    private ExhibitAuthorDto mapToExhibitAuthorDto(ExhibitAuthor exhibitAuthor){
        ExhibitAuthorDto exhibitAuthorDto = new ExhibitAuthorDto();
        exhibitAuthorDto.setAuthorId(exhibitAuthor.getAuthor().getId());
        exhibitAuthorDto.setExhibitId(exhibitAuthor.getExhibit().getId());
        if(exhibitAuthor.getCollection() != null) {
            exhibitAuthorDto.setCollectionId(exhibitAuthor.getCollection().getId());
        }
        return exhibitAuthorDto;
    }

    private ExhibitAuthor mapToExhibitAuthor(ExhibitAuthorDto exhibitAuthorDto){
        ExhibitAuthor exhibitAuthor;
        if(checkIfExists(exhibitAuthorDto.getExhibitId(), exhibitAuthorDto.getAuthorId())){
            exhibitAuthor = getExhibitAuthorById(exhibitAuthorDto.getExhibitId(), exhibitAuthorDto.getAuthorId()).get();
        } else {
            ExhibitAuthorId exhibitAuthorId = new ExhibitAuthorId(exhibitAuthorDto.getExhibitId(), exhibitAuthorDto.getAuthorId());
            exhibitAuthor = new ExhibitAuthor();
            if(exhibitAuthorDto.getExhibitId() != null){
                exhibitAuthor.setExhibit(exhibitService.getExhibitById(exhibitAuthorDto.getExhibitId()).get());
            }
            exhibitAuthor.setAuthor(authorService.getAuthorById(exhibitAuthorDto.getAuthorId()).get());
            exhibitAuthor.setId(exhibitAuthorId);
        }

        if(exhibitAuthorDto.getCollectionId() != null) {
            exhibitAuthor.setCollection(collectionService.getCollectionById(exhibitAuthorDto.getCollectionId()).get());
        } else {
            exhibitAuthor.setCollection(null);
        }

        return exhibitAuthor;
    }
    
}
