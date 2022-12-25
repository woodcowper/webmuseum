package com.webmuseum.museum.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webmuseum.museum.dto.CollectionDto;
import com.webmuseum.museum.entity.Author;
import com.webmuseum.museum.entity.Collection;
import com.webmuseum.museum.repository.AuthorRepository;
import com.webmuseum.museum.repository.CollectionRepository;
import com.webmuseum.museum.service.ICollectionService;

@Service
public class CollectionServiceImpl implements ICollectionService{

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public List<CollectionDto> findAllCollectionsForAuthor(long id) {
        List<Collection> collections = authorRepository.findById(id).get().getCollections();
        return collections.stream()
                .sorted((collection1, collection2) -> collection1.getName().compareTo(collection2.getName()))
                .map((collection) -> mapToCollectionDto(collection))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Collection> getCollectionById(long id) {
        return collectionRepository.findById(id);
    }

    @Override
    public CollectionDto getCollectionDtoById(long id) {
        return mapToCollectionDto(collectionRepository.findById(id).get());
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
        collectionRepository.save(mapToCollection(collection));
    }

    @Override
    public boolean checkIfExistsOthers(Long collectionId, String name, long authorId) {
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

    private CollectionDto mapToCollectionDto(Collection collection){
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collection.getId());
        collectionDto.setName(collection.getName());
        collectionDto.setDescription(collection.getDescription());
        collectionDto.setAuthorId(collection.getAuthor().getId());
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
        collection.setName(collectionDto.getName());
        collection.setDescription(collectionDto.getDescription());
        collection.setAuthor(author);

        return collection;
    }

}
