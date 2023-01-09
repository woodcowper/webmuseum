package com.webmuseum.museum.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.webmuseum.museum.dto.UserDto;
import com.webmuseum.museum.entity.User;
import com.webmuseum.museum.models.EmailDetails;
import com.webmuseum.museum.service.IEmailService;
import com.webmuseum.museum.service.IRoleService;
import com.webmuseum.museum.service.IUserService;
import com.webmuseum.museum.utils.EmailHelper;

import jakarta.validation.Valid;

@Controller
@RequestMapping("admin")
public class AdminController {
	final String CONTROLLER_VIEW_DIR = "admin/";

	@Autowired
	private IUserService userService;

	@Autowired
	private IRoleService roleService;

	@Autowired 
	private IEmailService emailService;

	 /* User */
	 @GetMapping("/user-list")
	 public String userList(Model model) {
		 model.addAttribute("users", userService.findAllUsers());
		 return CONTROLLER_VIEW_DIR + "user-list";
	 }
 
	 @GetMapping("/user-add")
	 public String userAdd(Model model) {
		 model.addAttribute("user", userService.createEmptyUserDtoForClient());
		 model.addAttribute("rolesList", roleService.findAllAvailableRoles());
		 return CONTROLLER_VIEW_DIR + "user-add";
	 }

	 @GetMapping("/user-gen-and-send-pass")
	 public String userGenAndSendPass(@RequestParam(name="id", required=true) long id, Model model) {
		String newPass = userService.generatePassword();

		if(userService.setNewPassword(id, newPass)){
			User user = userService.findUserById(id).get();
			EmailDetails emailDetails = new EmailDetails(user.getEmail(), "New password", EmailHelper.getNewPasswordTemplate(user.getEmail(), newPass));
			boolean status = emailService.sendSimpleMail(emailDetails);
			System.out.println("---STATUS: " + status + " SENDED TO " + user.getEmail());
		}
		
		return userList(model);
	 }
 
 
	 @GetMapping("/user-edit")
	 public String userEdit(@RequestParam long id, Model model) {
		 UserDto user = userService.getUserDtoById(id);
		 model.addAttribute("user", user);
		 model.addAttribute("rolesList", roleService.findAllAvailableRoles());
		 return CONTROLLER_VIEW_DIR + "user-add";
	 }
 
	 @PostMapping("/user-save")
	 public String userSave(@Valid @ModelAttribute("user") UserDto user, BindingResult result, Model model) { 
        if((user.getId() != null && userService.checkIfExistsOthers(user.getId(), user.getEmail())) || 
			(user.getId() == null && userService.findUserByEmail(user.getEmail()) != null)){
            result.rejectValue("email", null,
                    "There is already an account registered with the same email");
        } 
		if(user.getRoles().size() == 0){
			result.rejectValue("roles", null,
                    "Please select roles");
		}
		
		if (result.hasErrors()) {
			 model.addAttribute("user", user);
			 model.addAttribute("rolesList", roleService.findAllAvailableRoles());
			 return CONTROLLER_VIEW_DIR + "user-add";
		 }
 
		 userService.saveUser(user);
		 return "redirect:/" + CONTROLLER_VIEW_DIR + "user-list";
	 }
 
	 @GetMapping("/user-delete")
	 public String userDelete(@RequestParam(name="id", required=true) long id, Model model) {
		 userService.deleteUser(id);
		 return "redirect:/" + CONTROLLER_VIEW_DIR + "user-list";
	 }
 
	 /* END Manager */
	 /* -------------------------- */

}