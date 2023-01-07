package com.webmuseum.museum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webmuseum.museum.entity.Language;


public interface LanguageRepository extends JpaRepository<Language, Long> {
    
}
