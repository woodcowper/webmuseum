package com.webmuseum.museum.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.webmuseum.museum.dto.CollectionDto;
import com.webmuseum.museum.service.ICollectionService;

@Controller
@RequestMapping("ajax")
public class AjaxController {

    @Autowired
    private ICollectionService collectionService;

    @GetMapping("/get-collections")
    public ResponseEntity<?> getCollections(@RequestParam(name="authorId", required=true) long authorId, Model model) {
        List<CollectionDto> collections = collectionService.findAllCollectionsForAuthor(authorId);
        return ResponseEntity.ok(new Gson().toJson(collections));
    }
    
}
