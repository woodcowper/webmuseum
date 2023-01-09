package com.webmuseum.museum.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.webmuseum.museum.dto.ExhibitViewDto;
import com.webmuseum.museum.entity.Event;
import com.webmuseum.museum.service.ICategoryService;
import com.webmuseum.museum.service.IEventService;
import com.webmuseum.museum.service.IExhibitService;
import com.webmuseum.museum.utils.ResourceHelper;

@Controller
@RequestMapping("main")
public class MainController {
	final String CONTROLLER_VIEW_DIR = "main/";

	@Autowired
    private IExhibitService exhibitService;

	@Autowired
    private IEventService eventService;

	@Autowired
    private ICategoryService categoryService;

	@GetMapping({"/main", "/"})
	public String main(Model model) {
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

	@GetMapping("/event-list")
    public String eventList(@RequestParam(name="categoryId", required=false) Long categoryId, Model model) { 
		if(categoryId == null){
			model.addAttribute("events", eventService.findAllFuturesEvents());
		} else {
			model.addAttribute("events", eventService.findAllFuturesEventsForCategory(categoryId));
		}
		model.addAttribute("categories", categoryService.findAllEventCategories());
		return CONTROLLER_VIEW_DIR + "event-list";
    }

	@GetMapping("/event-view")
    public String eventView(@RequestParam Long eventId, Model model) {
		model.addAttribute("event", eventService.getEventViewDto(eventId));
		return CONTROLLER_VIEW_DIR + "event-view";
    }

}