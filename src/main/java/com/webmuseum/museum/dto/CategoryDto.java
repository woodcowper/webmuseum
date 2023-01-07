package com.webmuseum.museum.dto;

import com.webmuseum.museum.models.ECategoryType;
import com.webmuseum.museum.utils.LanguageHelper;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CategoryDto {

    private Long id;

    @NotEmpty
    @Size(min = 3)
    private String name;

    @NotNull
    private ECategoryType type;

    private Long languageId;

    public CategoryDto(ECategoryType type, long languageId) {
        this.type = type;
        this.languageId = languageId;
    }

    public CategoryDto() {
        this.type = ECategoryType.EVENT;
        this.languageId = LanguageHelper.DEFAULS_LANGUAGE_ID;
    }
}
