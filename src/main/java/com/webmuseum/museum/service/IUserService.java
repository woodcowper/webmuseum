package com.webmuseum.museum.service;

import java.util.List;

import com.webmuseum.museum.dto.UserDto;
import com.webmuseum.museum.entity.User;

public interface IUserService {
    void saveUser(UserDto userDto);

    User findUserByEmail(String email);

    void deleteUser(long id);

    List<UserDto> findAllUsers();

    List<UserDto> findAllManagers();

    List<UserDto> findAllClients();

    UserDto createEmptyUserDtoForClient();

    UserDto createEmptyUserDtoForManager();


}