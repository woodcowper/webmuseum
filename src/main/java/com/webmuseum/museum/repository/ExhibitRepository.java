package com.webmuseum.museum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.webmuseum.museum.entity.Exhibit;


public interface ExhibitRepository extends JpaRepository<Exhibit, Long> {
    
}
