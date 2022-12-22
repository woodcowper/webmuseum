package com.webmuseum.museum.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.webmuseum.museum.entity.Category;


public interface CategoryRepository extends JpaRepository<Category, Long> {
    
}

