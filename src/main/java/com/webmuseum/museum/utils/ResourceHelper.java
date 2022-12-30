package com.webmuseum.museum.utils;

import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.webmuseum.museum.controllers.ImageController;

public final class ResourceHelper {
    
    public static String getImgUrl(String fileName){
        return MvcUriComponentsBuilder
            .fromMethodName(ImageController.class, "getImage", fileName).build().toString();
    }

}
