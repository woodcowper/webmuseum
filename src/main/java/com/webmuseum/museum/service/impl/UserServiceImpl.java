package com.webmuseum.museum.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.webmuseum.museum.dto.UserDto;
import com.webmuseum.museum.entity.Role;
import com.webmuseum.museum.entity.User;
import com.webmuseum.museum.models.AppUserPrincipal;
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
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void saveUser(UserDto userDto) {
        saveUser(mapToUser(userDto));
    }

    @Override
    public void deleteUser(long id){
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.delete(user.get());
        }
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    
    @Override
    public UserDto getUserDtoById(long id) {
        User user = userRepository.findById(id).get();
        return mapToUserDto(user);
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
    public List<UserDto> findUsersEmailStartsWith(String text) {
        List<User> users = userRepository.findAll();
        return users.stream()
                .filter((user) -> user.getEmail().toLowerCase().startsWith(text.toLowerCase()))
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

    @Override
    public String generatePassword(){
        RandomStringGenerator pwdGenerator = new RandomStringGenerator.Builder().withinRange(33, 45)
        .build();
        return pwdGenerator.generate(12);
    }

    @Override
    public boolean setNewPassword(long id, String password){
        Optional<User> optUser = findUserById(id);
        if(optUser.isPresent()){
            User user = optUser.get();
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean checkIfExistsOthers(Long userId, String email) {
        return findAllUsers().stream()
                .filter((user) -> user.getEmail().equals(email)
                                        && user.getId() != userId)
                .findAny()
                .isPresent();
    }

    @Override
    public User getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof AppUserPrincipal){
            return findUserByEmail(auth.getName());
        } 
        return null;
    }

    @Override
    public UserDto getCurrentUserDto(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof AppUserPrincipal){
            return mapToUserDto(findUserByEmail(auth.getName()));
        } 
        return null;
    }

    private UserDto mapToUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setRoles(roleService.getRolesIds(user.getRoles()));
        userDto.setIsSuperAdmin(user.getRoles().contains(roleService.getSuperAdminRole()));
        userDto.setIsAdmin(user.getRoles().contains(roleService.getAdminRole()));
        userDto.setIsManager(user.getRoles().contains(roleService.getManagerRole()));
        userDto.setIsClient(user.getRoles().contains(roleService.getClientRole()));
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