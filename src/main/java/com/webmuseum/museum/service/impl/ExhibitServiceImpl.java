package com.webmuseum.museum.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.webmuseum.museum.controllers.MainController;
import com.webmuseum.museum.dto.AuthorViewDto;
import com.webmuseum.museum.dto.CollectionViewDto;
import com.webmuseum.museum.dto.ExhibitAuthorViewDto;
import com.webmuseum.museum.dto.ExhibitDto;
import com.webmuseum.museum.dto.ExhibitViewDto;
import com.webmuseum.museum.entity.AuthorDescription;
import com.webmuseum.museum.entity.CategoryDescription;
import com.webmuseum.museum.entity.CollectionDescription;
import com.webmuseum.museum.entity.Exhibit;
import com.webmuseum.museum.entity.ExhibitAuthor;
import com.webmuseum.museum.entity.ExhibitDescription;
import com.webmuseum.museum.repository.ExhibitRepository;
import com.webmuseum.museum.service.IAuthorService;
import com.webmuseum.museum.service.ICategoryService;
import com.webmuseum.museum.service.ICollectionService;
import com.webmuseum.museum.service.IExhibitAuthorService;
import com.webmuseum.museum.service.IExhibitService;
import com.webmuseum.museum.service.ILanguageService;
import com.webmuseum.museum.service.IStorageService;
import com.webmuseum.museum.utils.DateHelper;
import com.webmuseum.museum.utils.LanguageHelper;
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

    @Autowired 
    private ICollectionService collectionService;

    @Autowired 
    private IAuthorService authorService;

    @Autowired
    private ILanguageService languageService;

    @Override
    public List<ExhibitDto> findAllExhibits(){
        return findAllExhibits(LanguageHelper.DEFAULS_LANGUAGE_ID);
    }

    @Override
    public List<ExhibitDto> findAllExhibits(long languageId){
        return exhibitRepository.findAll().stream()
            .map((exhibit) -> mapToExhibitDto(exhibit, languageId))
            .sorted((exhibit1, exhibit2) -> exhibit1.getName().compareTo(exhibit2.getName()))
            .collect(Collectors.toList());
    }

    @Override
    public List<ExhibitDto> findAllExhibitsForAuthor(long id) {
        return findAllExhibitsForAuthor(id, LanguageHelper.DEFAULS_LANGUAGE_ID);
    }

    @Override
    public List<ExhibitDto> findAllExhibitsForAuthor(long id, long languageId) {
        return exhibitAuthorService.findAllForAuthor(id).stream()
            .map((exhibitAuthor) -> mapToExhibitDto(exhibitAuthor.getExhibit(), languageId))
            .collect(Collectors.toList());
    }
    @Override
    public Optional<Exhibit> getExhibitById(long id) {
        return exhibitRepository.findById(id);
    }

    @Override
    public ExhibitDto getExhibitDtoById(long id, long languageId) {
        return mapToExhibitDto(getExhibitById(id).get(), languageId);
    }

    @Override
    public void deleteExhibit(long id) {
        Optional<Exhibit> optExhibit = getExhibitById(id);
        if (optExhibit.isPresent()) {
            Exhibit exhibit = optExhibit.get();
            storageService.deleteImg(exhibit.getImgFileName());
            storageService.deleteQR(exhibit.getQrFileName());
            exhibitRepository.delete(exhibit);
        }
    }

    @Override
    public void saveExhibit(Exhibit exhibit) {
        if(exhibit.getId() == null){
            if(exhibit.getAuthors().size() > 0){
                List<ExhibitAuthor> authors = new ArrayList<ExhibitAuthor>(exhibit.getAuthors());
                exhibit.getAuthors().clear();
                exhibitRepository.save(exhibit);
                authors.stream().forEach(el -> el.setExhibit(exhibit));
                exhibit.getAuthors().addAll(authors);
            }
            exhibitRepository.save(exhibit);
            String fileName = storageService.storeQR(ResourceHelper.getUrl(MainController.class, "exhibitDetails", exhibit.getId(), null),  exhibit.getId().toString());
            exhibit.setQrFileName(fileName);
        }
        
        exhibitRepository.save(exhibit);
    }

    @Override
    public void saveExhibit(ExhibitDto exhibit) {
        if(exhibit.getImage() != null && !exhibit.getImage().isEmpty()){
            String fileName = storageService.storeImg(exhibit.getImage(), "");
            exhibit.setImgFileName(fileName);
        } else if(exhibit.getImgFileName().isEmpty()) {
            if(exhibit.getId() != null){
                Exhibit oldExhibit = getExhibitById(exhibit.getId()).get();
                if(oldExhibit.getImgFileName() != null && !oldExhibit.getImgFileName().equals(exhibit.getImgFileName())){
                    storageService.deleteImg(oldExhibit.getImgFileName());
                }
            }
            exhibit.setImgFileName(null);
        }
        saveExhibit(mapToExhibit(exhibit));
    }

    @Override
    public boolean checkIfExistsOthers(Long exhibitId, String name, Long authorId, long languageId) {
        return findAllExhibitsForAuthor(authorId, languageId).stream()
                .filter((exhibit) -> exhibit.getName().equals(name)
                                        && exhibit.getId() != exhibitId)
                .findAny()
                .isPresent();
    }

    @Override
    public ExhibitViewDto getExhibitViewDto(Long exhibitId){
        return getExhibitViewDto(exhibitId, LanguageHelper.DEFAULS_LANGUAGE_ID);
    }

    @Override
    public ExhibitViewDto getExhibitViewDto(Long exhibitId, Long languageId){
        Optional<Exhibit> exhibitOpt = getExhibitById(exhibitId);
		if(!exhibitOpt.isPresent()){
			// return error
		}
        // exhibit info
        Exhibit exhibit = exhibitOpt.get();
        ExhibitViewDto exhibitViewDto = new ExhibitViewDto();
        exhibitViewDto.setName(getName(exhibit, languageId));
        exhibitViewDto.setDescription(getDesc(exhibit, languageId));
        if(exhibit.getImgFileName() != null && !exhibit.getImgFileName().isEmpty()){
            exhibitViewDto.setImgUrl(ResourceHelper.getImgUrl(exhibit.getImgFileName()));
        }
        // categories info
        exhibitViewDto.setCategories(exhibit.getCategories().stream()
            .map((category) -> {
                CategoryDescription categoryDescription = categoryService.getDescription(category, languageId);
                if(categoryDescription != null){
                    return categoryDescription.getName();
                } else {
                    return null;
                }
            })
            .filter((category) -> category != null)
            .toList()
        );
        // exhibitAuthor info
        List<ExhibitAuthorViewDto> authors = new ArrayList<>();
        for(ExhibitAuthor authorExhibit : exhibit.getAuthors()){
            ExhibitAuthorViewDto exhibitAuthorViewDto = new ExhibitAuthorViewDto();
            // collection info
            if(authorExhibit.getCollection() != null){
                CollectionDescription collectionDescription = collectionService.getDescription(authorExhibit.getCollection(), languageId);
                if(collectionDescription != null){
                    exhibitAuthorViewDto.setCollection(new CollectionViewDto(collectionDescription.getName(), collectionDescription.getDescription()));
                }
            }
            // author info
            AuthorViewDto authorViewDto = new AuthorViewDto();
            AuthorDescription authorDescription = authorService.getDescription(authorExhibit.getAuthor(), languageId);
            if(authorDescription != null){
                authorViewDto.setName(authorDescription.getName());
                authorViewDto.setDescription(authorDescription.getDescription());
            }
            String datesLife = "";
            if(authorExhibit.getAuthor().getBirthDate() != null){
                datesLife += DateHelper.parseDateToStr(authorExhibit.getAuthor().getBirthDate());
                if(authorExhibit.getAuthor().getDieDate() != null){
                    datesLife += " - " + DateHelper.parseDateToStr(authorExhibit.getAuthor().getDieDate());
                }
            }
            authorViewDto.setDatesLife(datesLife);
            exhibitAuthorViewDto.setAuthor(authorViewDto);
            authors.add(exhibitAuthorViewDto);
        }
        exhibitViewDto.setAuthors(authors);
        return exhibitViewDto;
    }

    private ExhibitDto mapToExhibitDto(Exhibit exhibit, long languageId){
        ExhibitDto exhibitDto = new ExhibitDto();
        exhibitDto.setId(exhibit.getId());
        exhibitDto.setName(getName(exhibit, languageId));
        exhibitDto.setDescription(getDesc(exhibit, languageId));
        exhibitDto.setImgFileName(exhibit.getImgFileName());
        exhibitDto.setLanguageId(languageId);

        List<Long> categories = exhibit.getCategories().stream()
                                .map((category) -> category.getId())
                                .collect(Collectors.toList());
        exhibitDto.setCategories(categories);

        exhibitDto.setAuthors(exhibitAuthorService.findAllDtosForExhibit(exhibit.getId()));
        if(exhibit.getImgFileName() != null){
            exhibitDto.setImgUrl(ResourceHelper.getImgUrl(exhibit.getImgFileName()));
        }
        exhibitDto.setQrUrl(ResourceHelper.getQRUrl(exhibit.getQrFileName()));
        return exhibitDto;
    }

    private Exhibit mapToExhibit(ExhibitDto exhibitDto){
        Exhibit exhibit;
        if(exhibitDto.getId() == null){
            exhibit = new Exhibit();
        } else {
            exhibit = getExhibitById(exhibitDto.getId()).get();
        }
        ExhibitDescription exhibitDescription = getDescription(exhibit, exhibitDto.getLanguageId());
        if(exhibitDescription == null){
            exhibitDescription = new ExhibitDescription();
            exhibitDescription.setLanguage(languageService.getLanguageById(exhibitDto.getLanguageId()).get());
            exhibitDescription.setExhibit(exhibit);
            exhibit.getDescriptions().add(exhibitDescription);
        }
        exhibitDescription.setName(exhibitDto.getName());
        exhibitDescription.setDescription(exhibitDto.getDescription());

        exhibit.setImgFileName(exhibitDto.getImgFileName());

        exhibit.getCategories().clear();
        exhibit.getCategories().addAll(categoryService.findAllExhibitCategoriesWithIds(exhibitDto.getCategories()));
    
        exhibit.getAuthors().clear();
        exhibit.getAuthors().addAll(exhibitAuthorService.mapToExhibitAuthorsList(exhibitDto.getAuthors()));
        return exhibit;
    }

    @Override
    public ExhibitDescription getDescription(Exhibit exhibit, long languageId) {
        Optional<ExhibitDescription> description = exhibit.getDescriptions().stream()
                .filter((desc) -> desc.getLanguage().getId() == languageId)
                .findFirst();
        if (description.isPresent()) {
            return description.get();
        }
        return null;
    }

    private String getName(Exhibit exhibit, long languageId){
        ExhibitDescription exhibitDescription =  getDescription(exhibit, languageId);
        if(exhibitDescription == null){
            return "";
        }
        return exhibitDescription.getName();
    }

    private String getDesc(Exhibit exhibit, long languageId){
        ExhibitDescription exhibitDescription =  getDescription(exhibit, languageId);
        if(exhibitDescription == null){
            return "";
        }
        return exhibitDescription.getDescription();
    }
    
}
