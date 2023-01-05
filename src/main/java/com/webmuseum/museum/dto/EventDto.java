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
public class EventDto {
    private Long id;

    @NotEmpty
    @Size(min = 3)
    private String name;

    private String imgFileName;

    private String imgUrl;

    private MultipartFile image;

    @Size(max = 1000)
    private String description;

    private List<Long> categories = new ArrayList<>();

    private String date;
    
}
