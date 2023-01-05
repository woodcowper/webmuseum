package com.webmuseum.museum.controllers;

import java.util.Date;

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
import com.webmuseum.museum.dto.UserDto;
import com.webmuseum.museum.entity.User;
import com.webmuseum.museum.service.IRoleService;
import com.webmuseum.museum.service.IUserService;
import com.webmuseum.museum.utils.DateHelper;

import jakarta.validation.Valid;

@Controller
@RequestMapping("admin")
public class AdminController {
	final String CONTROLLER_VIEW_DIR = "admin/";

	@Autowired
	private IUserService userService;

	@Autowired
	private IRoleService roleService;

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
 
	//  @GetMapping("/manager-edit")
	//  public String managerEdit(@RequestParam long id, Model model) {
	// 	 UserDto manager = userService.getUserDtoById(id);
	// 	 model.addAttribute("manager", manager);
	// 	 return CONTROLLER_VIEW_DIR + "manager-add";
	//  }
 
	 @PostMapping("/manager-save")
	 public String managerSave(@Valid @ModelAttribute("manager") UserDto manager, BindingResult result, Model model) { 
		User existingUser = userService.findUserByEmail(manager.getEmail());

        if(existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()){
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