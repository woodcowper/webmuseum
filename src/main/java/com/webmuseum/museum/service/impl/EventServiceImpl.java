package com.webmuseum.museum.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webmuseum.museum.dto.EventDto;
import com.webmuseum.museum.dto.EventViewDto;
import com.webmuseum.museum.entity.Category;
import com.webmuseum.museum.entity.Event;
import com.webmuseum.museum.entity.User;
import com.webmuseum.museum.repository.EventRepository;
import com.webmuseum.museum.service.ICategoryService;
import com.webmuseum.museum.service.IEventService;
import com.webmuseum.museum.service.IStorageService;
import com.webmuseum.museum.service.IUserService;
import com.webmuseum.museum.utils.DateHelper;
import com.webmuseum.museum.utils.LanguageHelper;
import com.webmuseum.museum.utils.ResourceHelper;

@Service
public class EventServiceImpl implements IEventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired 
    private ICategoryService categoryService;

    @Autowired
    private IStorageService storageService;

    @Autowired
    private IUserService userService;


    @Override
    public List<Event> findEventsOnNextDay(){
        LocalDate todayDate = LocalDate.now().plusDays(1);
        Date nextDay = Date.from(todayDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return eventRepository.findAll().stream()
            .filter((event) -> event.getDate().compareTo(nextDay) == 0)
            .sorted((event1, event2) -> event1.getName().compareTo(event2.getName()))
            .collect(Collectors.toList());
    }
    @Override
    public List<Event> findEventsCreatedByLastDay(){
        LocalDate todayDate = LocalDate.now().minusDays(1);
        Date prevDay = Date.from(todayDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return eventRepository.findAll().stream()
            .filter((event) -> event.getCreatedAt().compareTo(prevDay) == 0)
            .sorted((event1, event2) -> event1.getName().compareTo(event2.getName()))
            .collect(Collectors.toList());
    }

    @Override
    public List<EventDto> findAllEventsForCurUser(){
        User curUser = userService.getCurrentUser();
        return curUser.getSubscribedEvents().stream()
            .sorted((event1, event2) -> event1.getDate().compareTo(event2.getDate()))
            .map((event) -> mapToEventDto(event))
            .collect(Collectors.toList());
    }

    @Override
    public List<EventDto> findAllEvents(){
        return eventRepository.findAll().stream()
            .sorted((event1, event2) -> event1.getName().compareTo(event2.getName()))
            .map((event) -> mapToEventDto(event))
            .collect(Collectors.toList());
    }

    @Override
    public List<EventDto> findAllEventsForCategory(long id){
        Category category = categoryService.getCategoryById(id).get();
        return category.getEvents().stream()
            .sorted((event1, event2) -> event1.getName().compareTo(event2.getName()))
            .map((event) -> mapToEventDto(event))
            .collect(Collectors.toList());
    }

    @Override
    public List<EventDto> findAllFuturesEvents(){
        Date curDate = new Date();
        return eventRepository.findAll().stream()
            .filter((event) -> event.getDate().compareTo(curDate) >= 0)
            .sorted((event1, event2) -> event1.getDate().compareTo(event2.getDate()))
            .map((event) -> mapToEventDto(event))
            .collect(Collectors.toList());
    }

    @Override
    public List<EventDto> findAllFuturesEventsForCategory(long id){
        Category category = categoryService.getCategoryById(id).get();
        Date curDate = new Date();
        return category.getEvents().stream()
            .filter((event) -> event.getDate().compareTo(curDate) > 0)
            .sorted((event1, event2) -> event1.getDate().compareTo(event2.getDate()))
            .map((event) -> mapToEventDto(event))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Event> getEventById(long id) {
        return eventRepository.findById(id);
    }

    @Override
    public EventDto getEventDtoById(long id) {
        return mapToEventDto(getEventById(id).get());
    }

    @Override
    public void deleteEvent(long id) {
        Optional<Event> event = getEventById(id);
        if (event.isPresent()) {
            eventRepository.delete(event.get());
        }
    }

    @Override
    public void saveEvent(Event event) {
        if(event.getId() == null){
            event.setCreatedAt(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        eventRepository.save(event);
    }

    @Override
    public void saveEvent(EventDto event) {
        if(event.getImage() != null && !event.getImage().isEmpty()){
            String fileName = storageService.storeImg(event.getImage(), "event");
            event.setImgFileName(fileName);
        } else if(event.getImgFileName().isEmpty()) {
            if(event.getId() != null){
                Event oldEvent = getEventById(event.getId()).get();
                if(oldEvent.getImgFileName() != null && !oldEvent.getImgFileName().equals(event.getImgFileName())){
                    storageService.deleteImg(oldEvent.getImgFileName());
                }
            }
            event.setImgFileName(null);
        }
        saveEvent(mapToEvent(event));
    }

    @Override
    public boolean checkIfExistsOthers(Long eventId, String name) {
        return findAllEvents().stream()
                .filter((event) -> event.getName().equals(name)
                                        && event.getId() != eventId)
                .findAny()
                .isPresent();
    }

    @Override
    public boolean subscribeEvent(User user, long eventId){
        if (user.getSubscribedEvents().stream().anyMatch((ev) -> ev.getId() == eventId)) {
            return true;
        }
        Optional<Event> optEvent = getEventById(eventId);
        if (!optEvent.isPresent()) {
            return false;
        }
        Event event = optEvent.get();
        event.getSubscribers().add(user);
        user.getSubscribedEvents().add(event);
        userService.saveUser(user);
        return true;
    }

    @Override
    public boolean unsubscribeEvent(User user, long eventId){
        if (!user.getSubscribedEvents().stream().anyMatch((ev) -> ev.getId() == eventId)) {
            return true;
        }
        Optional<Event> optEvent = getEventById(eventId);
        if (!optEvent.isPresent()) {
            return false;
        }
        Event event = optEvent.get();
        event.getSubscribers().remove(user);
        user.getSubscribedEvents().remove(event);
        userService.saveUser(user);
        return true;
    }

    private EventDto mapToEventDto(Event event){
        EventDto eventDto = new EventDto();
        eventDto.setId(event.getId());
        eventDto.setName(event.getName());
        eventDto.setDescription(event.getDescription());
        eventDto.setImgFileName(event.getImgFileName());
        eventDto.setDate(DateHelper.parseDateToStr(event.getDate()));

        List<Long> categories = event.getCategories().stream()
                                .map((category) -> category.getId())
                                .collect(Collectors.toList());
        eventDto.setCategories(categories);

        if(event.getImgFileName() != null){
            eventDto.setImgUrl(ResourceHelper.getImgUrl(event.getImgFileName()));
        }

        eventDto.setIsSubscribed(checkIfSubscribed(event.getId()));
        return eventDto;
    }

    private Event mapToEvent(EventDto eventDto){
        Event event;
        if(eventDto.getId() == null){
            event = new Event();
        } else {
            event = getEventById(eventDto.getId()).get();
        }
        event.setName(eventDto.getName());
        event.setDescription(eventDto.getDescription());
        event.setImgFileName(eventDto.getImgFileName());
        event.setDate(DateHelper.parseStrToDate(eventDto.getDate()));

        event.getCategories().clear();
        event.getCategories().addAll(categoryService.findAllEventCategoriesWithIds(eventDto.getCategories()));
        return event;
    }

    private Boolean checkIfSubscribed(Long eventId){
        User curUser = userService.getCurrentUser();
        if(curUser == null || eventId == null){
            return null;
        }
        return curUser.getSubscribedEvents().stream().anyMatch((ev) -> ev.getId() == eventId);   
    }

    @Override
    public EventViewDto getEventViewDto(Long eventId){
        Event event = getEventById(eventId).get();
        EventViewDto eventViewDto = new EventViewDto();

        eventViewDto.setName(event.getName());
        eventViewDto.setDescription(event.getDescription());
        eventViewDto.setDescription(event.getDescription());
        eventViewDto.setDate(DateHelper.parseDateToStr(event.getDate()));
        eventViewDto.setCategories(event.getCategories().stream()
            .map((category) -> categoryService.getDescription(category, LanguageHelper.DEFAULS_LANGUAGE_ID).getName())
            .toList()
        );
        
        if(event.getImgFileName() != null && !event.getImgFileName().isEmpty()){
            eventViewDto.setImgUrl(ResourceHelper.getImgUrl(event.getImgFileName()));
        }
        eventViewDto.setIsSubscribed(checkIfSubscribed(event.getId()));
        eventViewDto.setId(eventId);

        return eventViewDto;
    }
    
}
