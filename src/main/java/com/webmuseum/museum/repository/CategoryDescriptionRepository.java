package com.webmuseum.museum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webmuseum.museum.entity.CategoryDescription;


public interface CategoryDescriptionRepository extends JpaRepository<CategoryDescription, Long> {
    
}
