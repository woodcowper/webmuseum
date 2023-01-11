package com.webmuseum.museum.service;

import java.util.List;
import java.util.Optional;

import com.webmuseum.museum.dto.CollectionDto;
import com.webmuseum.museum.entity.Collection;
import com.webmuseum.museum.entity.CollectionDescription;

public interface ICollectionService {

    List<CollectionDto> findAllCollectionsForAuthor(long id);

    List<CollectionDto> findAllCollectionsForAuthor(long id, long languageId);

    Optional<Collection> getCollectionById(long id);

    CollectionDto getCollectionDtoById(long id, long languageId);

    void deleteCollection(long id);

    void saveCollection(Collection collection);

    void saveCollection(CollectionDto collection);

    boolean checkIfExistsOthers(Long collectionId, String name, long authorId, long languageId);

    CollectionDescription getDescription(Collection collection, long languageId);
}

