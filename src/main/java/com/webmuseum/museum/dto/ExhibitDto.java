package com.webmuseum.museum.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.webmuseum.museum.utils.LanguageHelper;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ExhibitDto {
    private Long id;

    @NotEmpty
    @Size(min = 3)
    private String name;

    private String imgFileName;

    private String imgUrl;

    private MultipartFile image;

    private String qrUrl;

    @Size(max = 1000)
    private String description;

    private List<ExhibitAuthorDto> authors = new ArrayList<>();

    private List<Long> categories = new ArrayList<>();

    private Long languageId;

    public ExhibitDto() {
        this.languageId = LanguageHelper.DEFAULS_LANGUAGE_ID;
    }

    public void clearEmptyAuthors(){
        authors.removeIf(author -> author.getAuthorId() == null);
    }
    
}
