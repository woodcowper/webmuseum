package com.webmuseum.museum.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.webmuseum.museum.entity.Collection;


public interface CollectionRepository extends JpaRepository<Collection, Long> {
    
}
