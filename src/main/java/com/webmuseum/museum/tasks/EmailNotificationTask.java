package com.webmuseum.museum.tasks;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.webmuseum.museum.entity.Category;
import com.webmuseum.museum.entity.Event;
import com.webmuseum.museum.entity.User;
import com.webmuseum.museum.models.EmailDetails;
import com.webmuseum.museum.service.ICategoryService;
import com.webmuseum.museum.service.IEmailService;
import com.webmuseum.museum.service.IEventService;
import com.webmuseum.museum.utils.EmailHelper;
import com.webmuseum.museum.utils.LanguageHelper;

@Component
public class EmailNotificationTask {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
	private IEventService eventService;

    @Autowired
	private IEmailService emailService;

    @Autowired
	private ICategoryService categoryService;

	@Scheduled(cron = "0 0 9 * * *")
	public void reportCurrentTime() {
        sendNotificationEventsOnNextDay();
        sendNotificationNewEventsInCategory();
	}

    public void sendNotificationEventsOnNextDay(){
        List<Event> events = eventService.findEventsOnNextDay();
        Set<User> subscribers = new HashSet<User>();
        events.stream().forEach((ev) -> {
            subscribers.addAll(ev.getSubscribers());
        });

        List<Event> eventsForUser = new ArrayList<Event>();
        String templateString = "";
        for(User subscriber : subscribers){
            eventsForUser.clear();
            templateString = "";
            events.stream().filter((ev) -> ev.getSubscribers().contains(subscriber))
                .forEach((ev) -> eventsForUser.add(ev));
            
            templateString = EmailHelper.getEventsOnNextDayTemplate(eventsForUser);
            emailService.sendSimpleMail(new EmailDetails(subscriber.getEmail(), "Event tomorrow", templateString));
        }

        events.stream().forEach((event) -> {
            event.getSubscribers().clear();
            eventService.saveEvent(event);
        });
    }

    public void sendNotificationNewEventsInCategory(){
        List<Event> events = eventService.findEventsCreatedByLastDay();
        Map<User, Set<Event>> eventsInfo = new HashMap<>();
        Map<User, Set<Category>> categoriesInfo = new HashMap<>();

        Set<Category> categories = new HashSet<Category>();
        events.stream().forEach((ev) -> categories.addAll(ev.getCategories()));

        categories.stream().forEach((cat) -> {
            cat.getSubscribers().forEach((user) -> {
                if(!eventsInfo.containsKey(user)){
                    eventsInfo.put(user, new HashSet<>());
                }
                eventsInfo.get(user).addAll(events.stream().filter((ev) -> ev.getCategories().contains(cat)).toList());
                if(!categoriesInfo.containsKey(user)){
                    categoriesInfo.put(user, new HashSet<>());
                }
                categoriesInfo.get(user).add(cat);
            });
        });
        String templateString = "";
        for(Entry<User, Set<Event>> notificationInfo : eventsInfo.entrySet()){
            String curCategories = categoriesInfo.get(notificationInfo.getKey()).stream()
                .map(cat -> categoryService.getDescription(cat, LanguageHelper.DEFAULS_LANGUAGE_ID).getName())
                .collect(Collectors.joining(","));

            templateString = EmailHelper.getNewEventsInCategoryTemplate(notificationInfo.getValue().stream().toList(), curCategories);
            emailService.sendSimpleMail(new EmailDetails(notificationInfo.getKey().getEmail(), "New events in categories: " + curCategories, templateString));
        }
    }
}
