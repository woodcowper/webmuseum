package com.webmuseum.museum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.webmuseum.museum.entity.Event;


public interface EventRepository extends JpaRepository<Event, Long> {
    
}
