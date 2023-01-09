package com.webmuseum.museum.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.webmuseum.museum.dto.SubscribersEventCategoryDto;
import com.webmuseum.museum.entity.Category;
import com.webmuseum.museum.entity.User;
import com.webmuseum.museum.service.ICategoryService;
import com.webmuseum.museum.service.IEventService;
import com.webmuseum.museum.service.IUserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("client")
public class ClientController {
    final String CONTROLLER_VIEW_DIR = "client/";

    @Autowired
	private IUserService userService;

    @Autowired
	private IEventService eventService;

    @Autowired
	private ICategoryService categoryService;


    @GetMapping("/user-info")
    public String userInfo(Model model) {
        model.addAttribute("user", userService.getCurrentUserDto());
        return CONTROLLER_VIEW_DIR + "user-info";
    }
        @GetMapping("/subscribed-events-list")
    public String subscribedEventsList(Model model) {
        model.addAttribute("events", eventService.findAllEventsForCurUser());
        return CONTROLLER_VIEW_DIR + "subscribed-events-list";
    }

    @GetMapping("/unsubscribe-event")
    public String unsubscribeEvent(@RequestParam long eventId, Model model) {
        User curUser = userService.getCurrentUser();
        eventService.unsubscribeEvent(curUser, eventId);
		return "redirect:/" + CONTROLLER_VIEW_DIR + "subscribed-events-list";
    }

    @GetMapping("/subscribed-category-list")
    public String subscribedCategoryList(Model model) {
        User curUser = userService.getCurrentUser();
        List<Long> subscribedCategoryIds = curUser.getSubscribedCategoryEvents().stream().map((cat) -> cat.getId()).toList();
        model.addAttribute("visibleAddBtn", categoryService.findAllEventCategoriesWithoutIds(subscribedCategoryIds).size() > 0);
        model.addAttribute("categories", categoryService.findAllEventCategoriesForCurUser());
        return CONTROLLER_VIEW_DIR + "subscribed-category-list";
    }

    @GetMapping("/subscribed-category-add")
    public String subscribedCategoryAdd(Model model) {
        User curUser = userService.getCurrentUser();
        List<Long> subscribedCategoryIds = curUser.getSubscribedCategoryEvents().stream().map((cat) -> cat.getId()).toList();
        model.addAttribute("categoriesList", categoryService.findAllEventCategoriesWithoutIds(subscribedCategoryIds));
        model.addAttribute("subscribedCategory", new SubscribersEventCategoryDto());
        return CONTROLLER_VIEW_DIR + "subscribed-category-add";
    }

    @PostMapping("/subscribed-category-save")
    public String subscribedCategorySave(@Valid @ModelAttribute("subscribed-category") SubscribersEventCategoryDto subscribedCategory, BindingResult result, Model model) {
        User curUser = userService.getCurrentUser();
        if(subscribedCategory.getCategories().isEmpty()){
            result.rejectValue("categories", null,
                    "Categories could not be empty");
        }

        if (result.hasErrors()) {
            List<Long> subscribedCategoryIds = curUser.getSubscribedCategoryEvents().stream().map((cat) -> cat.getId()).toList();
            model.addAttribute("categoriesList", categoryService.findAllEventCategoriesWithoutIds(subscribedCategoryIds));
            model.addAttribute("subscribedCategory", subscribedCategory);
            return CONTROLLER_VIEW_DIR + "subscribed-category-add";
        }

        for(Category category : categoryService.findAllEventCategoriesWithIds(subscribedCategory.getCategories())){
            categoryService.subscribeCategory(curUser, category.getId());
        }
        return "redirect:/" + CONTROLLER_VIEW_DIR + "subscribed-category-list";
    }

    @GetMapping("/unsubscribe-category")
    public String unsubscribeCategory(@RequestParam(name="id", required=true) long id, Model model) {
        User curUser = userService.getCurrentUser();
        categoryService.unsubscribeCategory(curUser, id);
        return "redirect:/" + CONTROLLER_VIEW_DIR + "subscribed-category-list";
    }

    
}
