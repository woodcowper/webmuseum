package com.webmuseum.museum.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webmuseum.museum.dto.CollectionDto;
import com.webmuseum.museum.entity.Author;
import com.webmuseum.museum.entity.Collection;
import com.webmuseum.museum.entity.CollectionDescription;
import com.webmuseum.museum.repository.AuthorRepository;
import com.webmuseum.museum.repository.CollectionRepository;
import com.webmuseum.museum.service.ICollectionService;
import com.webmuseum.museum.service.ILanguageService;
import com.webmuseum.museum.utils.LanguageHelper;

@Service
public class CollectionServiceImpl implements ICollectionService{

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ILanguageService languageService;

    @Override
    public List<CollectionDto> findAllCollectionsForAuthor(long id) {
        return findAllCollectionsForAuthor(id, LanguageHelper.DEFAULS_LANGUAGE_ID);
    }

    @Override
    public List<CollectionDto> findAllCollectionsForAuthor(long id, long languageId) {
        List<Collection> collections = authorRepository.findById(id).get().getCollections();
        return collections.stream()
                .map((collection) -> mapToCollectionDto(collection, languageId))
                .sorted((collection1, collection2) -> collection1.getName().compareTo(collection2.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Collection> getCollectionById(long id) {
        return collectionRepository.findById(id);
    }

    @Override
    public CollectionDto getCollectionDtoById(long id, long languageId) {
        return mapToCollectionDto(getCollectionById(id).get(), languageId);
    }

    @Override
    public void deleteCollection(long id) {
        Optional<Collection> collection = collectionRepository.findById(id);
        if (collection.isPresent()) {
            collectionRepository.delete(collection.get());
        }
    }

    @Override
    public void saveCollection(Collection collection) {
        collectionRepository.save(collection);
    }

    @Override
    public void saveCollection(CollectionDto collection) {
        saveCollection(mapToCollection(collection));
    }

    @Override
    public boolean checkIfExistsOthers(Long collectionId, String name, long authorId, long languageId) {
        if(collectionId != null){
            return findAllCollectionsForAuthor(authorId).stream()
                .filter(collection -> collection.getName().equals(name))
                .filter(collection -> collection.getId() != collectionId)
                .count() > 0;
        }
        return findAllCollectionsForAuthor(authorId).stream()
            .filter(collection -> collection.getName().equals(name))
            .count() > 0;
    }

    private CollectionDto mapToCollectionDto(Collection collection, long languageId){
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collection.getId());
        collectionDto.setName(getName(collection, languageId));
        collectionDto.setDescription(getDesc(collection, languageId));
        collectionDto.setAuthorId(collection.getAuthor().getId());
        collectionDto.setLanguageId(languageId);
        return collectionDto;
    }

    private Collection mapToCollection(CollectionDto collectionDto){
        Collection collection;
        if(collectionDto.getId() == null){
            collection = new Collection();
        } else {
            collection = getCollectionById(collectionDto.getId()).get();
        }
        Author author = authorRepository.getReferenceById(collectionDto.getAuthorId());
        collection.setAuthor(author);

        CollectionDescription collectionDescription = getDescription(collection, collectionDto.getLanguageId());
        if(collectionDescription == null){
            collectionDescription = new CollectionDescription();
            collectionDescription.setLanguage(languageService.getLanguageById(collectionDto.getLanguageId()).get());
            collectionDescription.setCollection(collection);
            collection.getDescriptions().add(collectionDescription);
        }
        collectionDescription.setName(collectionDto.getName());
        collectionDescription.setDescription(collectionDto.getDescription());

        return collection;
    }

    @Override
    public CollectionDescription getDescription(Collection collection, long languageId) {
        Optional<CollectionDescription> description = collection.getDescriptions().stream()
                .filter((desc) -> desc.getLanguage().getId() == languageId)
                .findFirst();
        if (description.isPresent()) {
            return description.get();
        }
        return null;
    }

    private String getName(Collection collection, long languageId){
        CollectionDescription collectionDescription =  getDescription(collection, languageId);
        if(collectionDescription == null){
            return "";
        }
        return collectionDescription.getName();
    }

    private String getDesc(Collection collection, long languageId){
        CollectionDescription collectionDescription =  getDescription(collection, languageId);
        if(collectionDescription == null){
            return "";
        }
        return collectionDescription.getDescription();
    }

}
