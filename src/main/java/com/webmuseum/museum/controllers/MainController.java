package com.webmuseum.museum.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.webmuseum.museum.dto.ExhibitViewDto;
import com.webmuseum.museum.service.IExhibitService;

@Controller
@RequestMapping("main")
public class MainController {
	final String CONTROLLER_VIEW_DIR = "main/";

	@Autowired
    private IExhibitService exhibitService;

	@GetMapping({"/main", "/"})
	public String main(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
		model.addAttribute("name", name);
		return CONTROLLER_VIEW_DIR + "main";
	}

	@GetMapping({"/exhibit-details-choose-lang", "/"})
	public String exhibitDetailsChooseLang(@RequestParam(name="exhibitId", required=true) Long exhibitId, Model model) {
		model.addAttribute("exhibitId", exhibitId);
		model.addAttribute("languages", exhibitService.getSupportedLanguages(exhibitId));
		return CONTROLLER_VIEW_DIR + "exhibit-details-choose-lang";
	}

	@GetMapping({"/exhibit-details", "/"})
	public String exhibitDetails(@RequestParam Long exhibitId, @RequestParam Long languageId, Model model) {
		ExhibitViewDto exhibit = exhibitService.getExhibitViewDto(exhibitId, languageId);		
		model.addAttribute("exhibit", exhibit);
		return CONTROLLER_VIEW_DIR + "exhibit-details";
	}

}