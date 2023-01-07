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
public class AuthorDto {
    private Long id;

    @NotEmpty
    @Size(min = 3)
    private String fullName;

    @Size(max = 1000)
    private String description;

    private String birthDate;

    private String dieDate;

    private Long languageId;

    public AuthorDto() {
        this.languageId = LanguageHelper.DEFAULS_LANGUAGE_ID;
    }


}
