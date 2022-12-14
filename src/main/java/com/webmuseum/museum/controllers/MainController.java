package com.webmuseum.museum.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
	final String CONTROLLER_VIEW_DIR = "main/";

	@GetMapping("/main")
	public String main(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
		model.addAttribute("name", name);
		return CONTROLLER_VIEW_DIR + "main";
	}

}