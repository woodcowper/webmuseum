package com.webmuseum.museum.service;

import java.util.List;
import java.util.Optional;

import com.webmuseum.museum.dto.EventDto;
import com.webmuseum.museum.entity.Event;

public interface IEventService {
    List<EventDto> findAllEvents();

    Optional<Event> getEventById(long id);

    EventDto getEventDtoById(long id);

    void deleteEvent(long id);

    void saveEvent(Event event);

    void saveEvent(EventDto event);

    boolean checkIfExistsOthers(Long eventId, String name);
}
