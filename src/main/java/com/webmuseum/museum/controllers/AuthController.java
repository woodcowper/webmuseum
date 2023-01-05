package com.webmuseum.museum.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.SpringVersion;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.webmuseum.museum.dto.UserDto;
import com.webmuseum.museum.entity.User;
import com.webmuseum.museum.service.IRoleService;
import com.webmuseum.museum.service.IUserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping ("auth")
public class AuthController {
	final String CONTROLLER_VIEW_DIR = "auth/";

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @GetMapping("/index")
    public String index(){
        return CONTROLLER_VIEW_DIR + "index";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        UserDto user = userService.createEmptyUserDtoForClient();
        model.addAttribute("user", user);
        return CONTROLLER_VIEW_DIR + "register";
    }

    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               Model model){
        
        if(userDto.getPassword().isEmpty()){
            result.rejectValue("password", null,
                    "Password should not be empty");
        }

        User existingUser = userService.findUserByEmail(userDto.getEmail());
        if(existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()){
            result.rejectValue("email", null,
                    "There is already an account registered with the same email");
        }

        if(result.hasErrors()){
            model.addAttribute("user", userDto);
            return CONTROLLER_VIEW_DIR + "register";
        }

        userDto.setRoles(List.of(roleService.getClientRole().getId()));
        userService.saveUser(userDto);
        return "redirect:/" + CONTROLLER_VIEW_DIR + "register?success";
    }

    @GetMapping("/users")
    public String users(Model model){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("cur_user", ((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        return CONTROLLER_VIEW_DIR + "users";
    }

    @GetMapping("/login")
    public String loginForm(){
        return CONTROLLER_VIEW_DIR + "login";
    }
}