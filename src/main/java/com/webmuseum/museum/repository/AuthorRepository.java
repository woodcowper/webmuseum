package com.webmuseum.museum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.webmuseum.museum.entity.Author;


public interface AuthorRepository extends JpaRepository<Author, Long> {
    
}
