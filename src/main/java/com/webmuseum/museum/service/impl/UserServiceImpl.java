package com.webmuseum.museum.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.webmuseum.museum.dto.UserDto;
import com.webmuseum.museum.entity.Role;
import com.webmuseum.museum.entity.User;
import com.webmuseum.museum.repository.RoleRepository;
import com.webmuseum.museum.repository.UserRepository;
import com.webmuseum.museum.service.IRoleService;
import com.webmuseum.museum.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IRoleService roleService;

    @Override
    public void saveUser(UserDto userDto) {
        User user = mapToUser(userDto);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(long id){
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.delete(user.get());
        }
    }


    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> findAllManagers() {
        Role role = roleService.getManagerRole();
        return findAllByRole(role);
    }

    @Override
    public List<UserDto> findAllClients() {
        Role role = roleService.getClientRole();
        return findAllByRole(role);
    }

    @Override
    public UserDto createEmptyUserDtoForClient() {
        UserDto userDto = new UserDto();
        userDto.setRoles(List.of(roleService.getClientRole().getId()));
        return userDto;
    }

    @Override
    public UserDto createEmptyUserDtoForManager() {
        UserDto userDto = new UserDto();
        userDto.setRoles(List.of(roleService.getClientRole().getId(), roleService.getManagerRole().getId()));
        return userDto;
    }

    private UserDto mapToUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setRoles(roleService.getRolesIds(user.getRoles()));
        return userDto;
    }

    private User mapToUser(UserDto userDto){
        User user;
        if(userDto.getId() == null){
            user = new User();
        } else {
            user = userRepository.findById(userDto.getId()).get();
        }
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        if(userDto.getPassword() != null && !userDto.getPassword().isEmpty()){
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        user.getRoles().clear();
        user.getRoles().addAll(roleService.getRolesByIds(userDto.getRoles()));
        return user;
    }

    private List<UserDto> findAllByRole(Role role){
        if(role == null){
            return new ArrayList<UserDto>();
        }
        return role.getUsers().stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());

    }

}