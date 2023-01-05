package com.webmuseum.museum.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty(message = "Email should not be empty")
    @Email
    private String email;
    
    private String password;

    private List<Long> roles = new ArrayList();
}