package com.webmuseum.museum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitAuthorViewDto {
    private AuthorViewDto author;

    private CollectionViewDto collection;
}
