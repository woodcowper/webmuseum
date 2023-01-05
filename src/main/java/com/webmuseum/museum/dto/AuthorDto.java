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
public class AuthorDto {
    private Long id;

    @NotEmpty
    @Size(min = 3)
    private String fullName;

    @Size(max = 1000)
    private String description;

    private String birthDate;

    private String dieDate;

}
