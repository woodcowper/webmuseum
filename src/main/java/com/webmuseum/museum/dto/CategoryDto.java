package com.webmuseum.museum.dto;

import com.webmuseum.museum.entity.CategoryType;

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
    private CategoryType type;

    public CategoryDto(CategoryType type) {
        this.type = type;
    }

    public CategoryDto() {
        this.type = CategoryType.EVENT;
    }
}
