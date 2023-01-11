package com.webmuseum.museum.utils;

import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.webmuseum.museum.controllers.ImageController;

public final class ResourceHelper {

    private final static String SERVER_IP = "192.168.0.73";
    
    public static String getImgUrl(String fileName){
        return MvcUriComponentsBuilder
            .fromMethodName(ImageController.class, "getImage", fileName).build().toString();
    }

    public static String getQRUrl(String fileName){
        return MvcUriComponentsBuilder
            .fromMethodName(ImageController.class, "getQR", fileName).build().toString();
    }

    public static String getUrl(Class controllerClass, String method, Object ...args){
        String uri = MvcUriComponentsBuilder
            .fromMethodName(controllerClass, method, args).toUriString();
        if(!SERVER_IP.isEmpty()){
            uri = uri.replaceAll("localhost:", SERVER_IP + ":");
        }
        return uri;
    }

    public static String getEventUrl(Long id){
        String uri = "http://localhost:8080/main/event-view?eventId=" + id;
        if(!SERVER_IP.isEmpty()){
            uri = uri.replaceAll("localhost:", SERVER_IP + ":");
        }
        return uri;
    }

}
