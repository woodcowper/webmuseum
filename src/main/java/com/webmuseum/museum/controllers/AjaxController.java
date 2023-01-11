package com.webmuseum.museum.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.webmuseum.museum.dto.CollectionDto;
import com.webmuseum.museum.entity.Event;
import com.webmuseum.museum.entity.User;
import com.webmuseum.museum.service.ICollectionService;
import com.webmuseum.museum.service.IEventService;
import com.webmuseum.museum.service.IUserService;

@Controller
@RequestMapping("ajax")
public class AjaxController {

    @Autowired
    private ICollectionService collectionService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IEventService eventService;

    @GetMapping("/get-collections")
    public ResponseEntity<?> getCollections(@RequestParam(name="authorId", required=true) long authorId, Model model) {
        List<CollectionDto> collections = collectionService.findAllCollectionsForAuthor(authorId);
        return ResponseEntity.ok(new Gson().toJson(collections));
    }

    @GetMapping("/subscribe-event")
    public ResponseEntity<?> subscribeEvent(@RequestParam(name = "eventId", required = true) long eventId,
            Model model) {
        User curUser = userService.getCurrentUser();
        if (curUser == null) {
            ResponseEntity.ok(new Gson().toJson(false));
        }

        return ResponseEntity.ok(new Gson().toJson(eventService.subscribeEvent(curUser, eventId)));
    }

    @GetMapping("/unsubscribe-event")
    public ResponseEntity<?> unsubscribeEvent(@RequestParam(name = "eventId", required = true) long eventId,
            Model model) {
        User curUser = userService.getCurrentUser();
        if (curUser == null) {
            ResponseEntity.ok(new Gson().toJson(false));
        }
        
        return ResponseEntity.ok(new Gson().toJson(eventService.unsubscribeEvent(curUser, eventId)));
    }
    
}
