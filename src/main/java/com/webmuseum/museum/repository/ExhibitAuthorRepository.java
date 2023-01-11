package com.webmuseum.museum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.webmuseum.museum.entity.ExhibitAuthor;

public interface ExhibitAuthorRepository extends JpaRepository<ExhibitAuthor, Long> {
    
}
