package com.webmuseum.museum.service;

import java.util.List;
import java.util.Optional;

import com.webmuseum.museum.dto.UserDto;
import com.webmuseum.museum.entity.User;

public interface IUserService {
    void saveUser(UserDto userDto);

    void saveUser(User userDto);

    Optional<User> findUserById(Long id);

    UserDto getUserDtoById(long id);

    User findUserByEmail(String email);

    void deleteUser(long id);

    List<UserDto> findAllUsers();

    List<UserDto> findUsersEmailStartsWith(String text);

    List<UserDto> findAllManagers();

    List<UserDto> findAllClients();

    UserDto createEmptyUserDtoForClient();

    UserDto createEmptyUserDtoForManager();

    String generatePassword();

    boolean setNewPassword(long id, String password);

    boolean checkIfExistsOthers(Long userId, String email);

    User getCurrentUser();

    UserDto getCurrentUserDto();


}