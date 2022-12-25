package com.webmuseum.museum.service;

import java.util.List;
import java.util.Optional;

import com.webmuseum.museum.dto.CollectionDto;
import com.webmuseum.museum.entity.Collection;

public interface ICollectionService {

    List<CollectionDto> findAllCollectionsForAuthor(long id);

    Optional<Collection> getCollectionById(long id);

    CollectionDto getCollectionDtoById(long id);

    void deleteCollection(long id);

    void saveCollection(Collection collection);

    void saveCollection(CollectionDto collection);

    boolean checkIfExistsOthers(Long collectionId, String name, long authorId);
}

