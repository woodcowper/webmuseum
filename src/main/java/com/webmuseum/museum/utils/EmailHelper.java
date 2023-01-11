package com.webmuseum.museum.utils;

import java.util.List;

import com.webmuseum.museum.controllers.MainController;
import com.webmuseum.museum.entity.Event;

public final class EmailHelper {

    public static String getNewPasswordTemplate(String username, String password){
        return username + ", your new password is: " + password;
    }

    public static String getNewEventsInCategoryTemplate(List<Event> events, String categoryName){
        String template = String.join("\n",
            "New events in category: " + categoryName,
            "Events: "
        );
        String url = "";
        for (Event event : events) {
            url = ResourceHelper.getEventUrl(event.getId());
            template += " - " + event.getName() + " : " + url + "\n";
        }
        
        return template;
    }

    public static String getEventsOnNextDayTemplate(List<Event> events){
        String template = String.join("\n",
            "Events tomorrow : "
        );
        String url = "";
        for (Event event : events) {
            url = ResourceHelper.getEventUrl(event.getId());
            template += " - " + event.getName() + " : " + url + "\n";
        }
        
        return template;
    }
    
}
