package com.webmuseum.museum.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitViewDto {
    private String name;

    private String imgUrl;

    private String description;

    private List<ExhibitAuthorViewDto> authors = new ArrayList<>();

    private List<String> categories = new ArrayList<>();
}
