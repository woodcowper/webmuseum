package com.webmuseum.museum.dto;

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
public class CollectionDto {

    private Long id;

    @NotEmpty
    @Size(min = 3)
    private String name;

    private String description;

    private Long authorId;
    
}
