package com.webmuseum.museum.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventViewDto {
    private Long id;
    
    private String name;

    private String imgUrl;

    private String description;

    private List<String> categories = new ArrayList<>();

    private String date;

    private Boolean isSubscribed;
    
}
