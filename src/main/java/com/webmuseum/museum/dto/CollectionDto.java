package com.webmuseum.museum.dto;

import com.webmuseum.museum.utils.LanguageHelper;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CollectionDto {

    private Long id;

    @NotEmpty
    @Size(min = 3)
    private String name;

    @Size(max = 1000)
    private String description;

    private Long authorId;

    private Long languageId;

    public CollectionDto() {
        this.languageId = LanguageHelper.DEFAULS_LANGUAGE_ID;
    }
    
}
