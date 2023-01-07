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

	 /* Manager */
	 @GetMapping("/manager-list")
	 public String managerList(Model model) {
		 model.addAttribute("managers", userService.findAllManagers());
		 return CONTROLLER_VIEW_DIR + "manager-list";
	 }
 
	 @GetMapping("/manager-add")
	 public String managerAdd(Model model) {
		 model.addAttribute("manager", userService.createEmptyUserDtoForManager());
		 model.addAttribute("rolesList", roleService.findAllAvailableRoles());
		 return CONTROLLER_VIEW_DIR + "manager-add";
	 }

	 @GetMapping("/manager-gen-and-send-pass")
	 public String managerGenAndSendPass(@RequestParam(name="id", required=true) long id, Model model) {
		String newPass = userService.generatePassword();

		if(userService.setNewPassword(id, newPass)){
			User user = userService.findUserById(id).get();
			EmailDetails emailDetails = new EmailDetails(user.getEmail(), "New password", EmailHelper.getNewPasswordTemplate(user.getEmail(), newPass));
			boolean status = emailService.sendSimpleMail(emailDetails);
			System.out.println("---STATUS: " + status + " SENDED TO " + user.getEmail());
		}
		
		return managerList(model);
	 }
 
 
	 @GetMapping("/manager-edit")
	 public String managerEdit(@RequestParam long id, Model model) {
		 UserDto manager = userService.getUserDtoById(id);
		 model.addAttribute("manager", manager);
		 model.addAttribute("rolesList", roleService.findAllAvailableRoles());
		 return CONTROLLER_VIEW_DIR + "manager-add";
	 }
 
	 @PostMapping("/manager-save")
	 public String managerSave(@Valid @ModelAttribute("manager") UserDto manager, BindingResult result, Model model) { 
        if(manager.getId() != null && userService.checkIfExistsOthers(manager.getId(), manager.getEmail())){
            result.rejectValue("email", null,
                    "There is already an account registered with the same email");
        } 
		
		if (result.hasErrors()) {
			 model.addAttribute("manager", manager);
			 return CONTROLLER_VIEW_DIR + "manager-add";
		 }
 
		 userService.saveUser(manager);
		 return "redirect:/" + CONTROLLER_VIEW_DIR + "manager-list";
	 }
 
	 @GetMapping("/manager-delete")
	 public String managerDelete(@RequestParam(name="id", required=true) long id, Model model) {
		 userService.deleteUser(id);
		 return "redirect:/" + CONTROLLER_VIEW_DIR + "manager-list";
	 }
 
	 /* END Manager */
	 /* -------------------------- */

}