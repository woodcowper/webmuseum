package com.webmuseum.museum.controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.webmuseum.museum.dto.AuthorDto;
import com.webmuseum.museum.dto.CategoryDto;
import com.webmuseum.museum.dto.CollectionDto;
import com.webmuseum.museum.dto.EventDto;
import com.webmuseum.museum.dto.ExhibitAuthorDto;
import com.webmuseum.museum.dto.ExhibitDto;
import com.webmuseum.museum.entity.Author;
import com.webmuseum.museum.models.ECategoryType;
import com.webmuseum.museum.service.IAuthorService;
import com.webmuseum.museum.service.ICategoryService;
import com.webmuseum.museum.service.ICollectionService;
import com.webmuseum.museum.service.IEventService;
import com.webmuseum.museum.service.IExhibitService;
import com.webmuseum.museum.service.ILanguageService;
import com.webmuseum.museum.utils.DateHelper;
import com.webmuseum.museum.utils.LanguageHelper;

import jakarta.validation.Valid;

@Controller
@RequestMapping ("manager")
public class ManagerController {
    final String CONTROLLER_VIEW_DIR = "manager/";

    @Autowired
    private IAuthorService authorService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ICollectionService collectionService;

    @Autowired
    private IExhibitService exhibitService;

    @Autowired
    private IEventService eventService;

    @Autowired
    private ILanguageService languageService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    /* Index */
    @GetMapping({"/index", "/", ""})
    public String index(Model model) {
        return CONTROLLER_VIEW_DIR + "index";
    }

    /* Author */
    @GetMapping("/author-list")
    public String authorList(Model model) {
        model.addAttribute("authors", authorService.findAllAuthors());
        model.addAttribute("languages", languageService.findAllLanguagesWithoutId(LanguageHelper.DEFAULS_LANGUAGE_ID));
        return CONTROLLER_VIEW_DIR + "author-list";
    }


    @GetMapping("/author-add")
    public String authorAdd(Model model) {
        model.addAttribute("author", new AuthorDto());
        return CONTROLLER_VIEW_DIR + "author-add";
    }

    @GetMapping("/author-edit")
    public String authorEdit(@RequestParam long id, Model model) {
        AuthorDto author = authorService.getAuthorDtoById(id, LanguageHelper.DEFAULS_LANGUAGE_ID);
        model.addAttribute("author", author);
        return CONTROLLER_VIEW_DIR + "author-add";
    }

    @PostMapping("/author-save")
    public String authorSave(@Valid @ModelAttribute("author") AuthorDto author, BindingResult result, Model model) {

        Date birthDate = DateHelper.parseStrToDate(author.getBirthDate());
        Date dieDate = DateHelper.parseStrToDate(author.getDieDate());
        if(birthDate != null && dieDate != null && birthDate.compareTo(dieDate) >= 0){
            result.rejectValue("birthDate", null,
                    "Birth date must be biggest then die date");
        }

        if (result.hasErrors()) {
            model.addAttribute("author", author);
            model.addAttribute("isTranslations", author.getLanguageId() != LanguageHelper.DEFAULS_LANGUAGE_ID);
            return CONTROLLER_VIEW_DIR + "author-add";
        }

        authorService.saveAuthor(author);
        return "redirect:/" + CONTROLLER_VIEW_DIR + "author-list";
    }

    @GetMapping("/author-translation")
    public String authorTranslation(@RequestParam long authorId, @RequestParam long languageId, Model model) {
        AuthorDto author = authorService.getAuthorDtoById(authorId, languageId);
        model.addAttribute("author", author);
        model.addAttribute("isTranslations", true);
        return CONTROLLER_VIEW_DIR + "author-add";
    }

    @GetMapping("/author-delete")
    public String authorDelete(@RequestParam(name="id", required=true) long id, Model model) {
        authorService.deleteAuthor(id);
        return "redirect:/" + CONTROLLER_VIEW_DIR + "author-list";
    }

    /* END Author */
    /* -------------------------- */

    /* Event category */
    @GetMapping("/category-event-list")
    public String categoryEventList(Model model) {
        model.addAttribute("categories", categoryService.findAllEventCategories());
        model.addAttribute("type", "event");
        model.addAttribute("languages", languageService.findAllLanguagesWithoutId(LanguageHelper.DEFAULS_LANGUAGE_ID));
        return CONTROLLER_VIEW_DIR + "category-list";
    }

    @GetMapping("/category-event-add")
    public String categoryEventAdd(Model model) {
        return categoryAdd(model, ECategoryType.EVENT);
    }
    /* END Event category */

    /* Exhibit category */

    @GetMapping("/category-exhibit-list")
    public String categoryExhibitList(Model model) {
        model.addAttribute("categories", categoryService.findAllExhibitCategories());
        model.addAttribute("type", "exhibit");
        model.addAttribute("languages", languageService.findAllLanguagesWithoutId(LanguageHelper.DEFAULS_LANGUAGE_ID));
        return CONTROLLER_VIEW_DIR + "category-list";
    }

    @GetMapping("/category-exhibit-add")
    public String categoryExhibitAdd(Model model) {
        return categoryAdd(model, ECategoryType.EXHIBIT);
    }
    /* END Exhibit category */

    /* Category */
    private String categoryAdd(Model model, ECategoryType type) {
        return categoryAdd(model, type, LanguageHelper.DEFAULS_LANGUAGE_ID);
    }

    private String categoryAdd(Model model, ECategoryType type, long languageId) {
        model.addAttribute("category", new CategoryDto(type, languageId));
        model.addAttribute("type", type.name());
        return CONTROLLER_VIEW_DIR + "category-add";
    }

    @GetMapping("/category-edit")
    public String categoryEdit(@RequestParam long id, Model model) {
        CategoryDto category = categoryService.getCategoryDtoById(id);
        model.addAttribute("category", category);
        model.addAttribute("type", categoryService.getFormattedCategoryTypeName(category));
        return CONTROLLER_VIEW_DIR + "category-add";
    }

    @GetMapping("/category-translation")
    public String categoryTranslation(@RequestParam long categoryId, @RequestParam long languageId, Model model) {
        CategoryDto category = categoryService.getCategoryDtoById(categoryId, languageId);
        model.addAttribute("category", category);
        model.addAttribute("type", categoryService.getFormattedCategoryTypeName(category));
        model.addAttribute("isTranslations", true);
        return CONTROLLER_VIEW_DIR + "category-add";
    }

    @PostMapping("/category-save")
    public String categorySave(@Valid @ModelAttribute("category") CategoryDto category, BindingResult result, Model model) {
        if(categoryService.checkIfExistsOthers(category.getId(), category.getName(), category.getType(), category.getLanguageId())){
            result.rejectValue("name", null,
                    "There is already category for " + categoryService.getFormattedCategoryTypeName(category) +  " added with the same name");
        }

        if (result.hasErrors()) {
            model.addAttribute("category", category);
            model.addAttribute("type", categoryService.getFormattedCategoryTypeName(category));
            return CONTROLLER_VIEW_DIR + "category-add";
        }

        categoryService.saveCategory(category);
        return "redirect:/" + CONTROLLER_VIEW_DIR + "category-" + categoryService.getFormattedCategoryTypeName(category) +  "-list";
    }

    @GetMapping("/category-delete")
    public String categoryDelete(@RequestParam(name="id", required=true) long id, Model model) {
        String categoryTypeName = categoryService.getFormattedCategoryTypeName(categoryService.getCategoryById(id).get());
        categoryService.deleteCategory(id);
        return "redirect:/" + CONTROLLER_VIEW_DIR + "category-" + categoryTypeName + "-list";
    }

    /* END Category */
    /* -------------------------- */

    /* Collection */

    @GetMapping("/collection-list")
    public String collectionList(@RequestParam(name="authorId", required=true) long authorId, Model model) {
        model.addAttribute("collections", collectionService.findAllCollectionsForAuthor(authorId));
        model.addAttribute("authorId", authorId);
        model.addAttribute("languages", languageService.findAllLanguagesWithoutId(LanguageHelper.DEFAULS_LANGUAGE_ID));
        return CONTROLLER_VIEW_DIR + "collection-list";
    }

    @GetMapping("/collection-add")
    private String collectionAdd(@RequestParam(name="authorId", required=true) long authorId, Model model) {
        CollectionDto collection = new CollectionDto();
        collection.setLanguageId(LanguageHelper.DEFAULS_LANGUAGE_ID);
        collection.setAuthorId(authorId);
        model.addAttribute("collection", collection);
        return CONTROLLER_VIEW_DIR + "collection-add";
    }

    @GetMapping("/collection-edit")
    public String collectionEdit(@RequestParam long id, Model model) {
        CollectionDto collection = collectionService.getCollectionDtoById(id, LanguageHelper.DEFAULS_LANGUAGE_ID);
        model.addAttribute("collection", collection);
        return CONTROLLER_VIEW_DIR + "collection-add";
    }

    @PostMapping("/collection-save")
    public String collectionSave(@Valid @ModelAttribute("collection") CollectionDto collection, BindingResult result, Model model) {
        Author author = authorService.getAuthorById(collection.getAuthorId()).get();
        if(collectionService.checkIfExistsOthers(collection.getId(), collection.getName(), collection.getAuthorId(), collection.getLanguageId())){
            result.rejectValue("name", null,
                    "There is already collection for " + authorService.getDescription(author, LanguageHelper.DEFAULS_LANGUAGE_ID) +  " added with the same name");
        }

        if (result.hasErrors()) {
            model.addAttribute("collection", collection);
            return CONTROLLER_VIEW_DIR + "collection-add";
        }
        
        collectionService.saveCollection(collection);
        return "redirect:/" + CONTROLLER_VIEW_DIR + "collection-list?authorId=" + collection.getAuthorId();
    }

    @GetMapping("/collection-translation")
    public String collectionTranslation(@RequestParam long collectionId, @RequestParam long languageId, Model model) {
        CollectionDto collection = collectionService.getCollectionDtoById(collectionId, languageId);
        model.addAttribute("collection", collection);
        model.addAttribute("isTranslations", true);
        return CONTROLLER_VIEW_DIR + "collection-add";
    }

    @GetMapping("/collection-delete")
    public String collectionDelete(@RequestParam(name="id", required=true) long id, Model model) {
        long authorId = collectionService.getCollectionById(id).get().getAuthor().getId();
        collectionService.deleteCollection(id);
        return "redirect:/" + CONTROLLER_VIEW_DIR + "collection-list?authorId=" + authorId;
    }

    /* END Collection */
    /* -------------------------- */

    /* Exhibit */
    @GetMapping("/exhibit-list")
    public String exhibitList(Model model) {
        model.addAttribute("exhibits", exhibitService.findAllExhibits());
        return CONTROLLER_VIEW_DIR + "exhibit-list";
    }

    @GetMapping("/exhibit-author-list")
    public String exhibitList(@RequestParam(name="authorId", required=true) long authorId, Model model) {
        model.addAttribute("exhibits", exhibitService.findAllExhibitsForAuthor(authorId));
        return CONTROLLER_VIEW_DIR + "exhibit-list";
    }

    @GetMapping("/exhibit-add")
    private String exhibitAdd(Model model) {
        ExhibitDto exhibit = new ExhibitDto();
        model.addAttribute("exhibit", exhibit);
        model.addAttribute("categoriesList", categoryService.findAllExhibitCategories());
        model.addAttribute("authorsList", authorService.findAllAuthors());
        model.addAttribute("redirectUrl", "exhibit-list"); //TODO
        return CONTROLLER_VIEW_DIR + "exhibit-add";
    }

    @GetMapping("/exhibit-edit")
    public String exhibitEdit(@RequestParam long id, Model model) {
        ExhibitDto exhibit = exhibitService.getExhibitDtoById(id);
        model.addAttribute("exhibit", exhibit);
        model.addAttribute("categoriesList", categoryService.findAllExhibitCategories());
        model.addAttribute("authorsList", authorService.findAllAuthors());
        return CONTROLLER_VIEW_DIR + "exhibit-add";
    }

    @PostMapping("/exhibit-save")
    public String exhibitSave(@Valid @ModelAttribute("exhibit") ExhibitDto exhibit, BindingResult result, Model model) {
        exhibit.clearEmptyAuthors();
        for(ExhibitAuthorDto exhibitAthor: exhibit.getAuthors()){
            if(exhibitService.checkIfExistsOthers(exhibit.getId(), exhibit.getName(), exhibitAthor.getAuthorId())){
                Author author = authorService.getAuthorById(exhibitAthor.getAuthorId()).get();
                result.rejectValue("name", null,
                    "There is already exhibit for " + authorService.getDescription(author, LanguageHelper.DEFAULS_LANGUAGE_ID).getName() +  " added with the same name");
                break;
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("exhibit", exhibit);
            model.addAttribute("categoriesList", categoryService.findAllExhibitCategories());
            model.addAttribute("authorsList", authorService.findAllAuthors());

            return CONTROLLER_VIEW_DIR + "exhibit-add";
        }
        
        exhibitService.saveExhibit(exhibit);
        return "redirect:/" + CONTROLLER_VIEW_DIR + "exhibit-list";
    }

    @GetMapping("/exhibit-delete")
    public String exhibitDelete(@RequestParam(name="id", required=true) long id, Model model) {
        exhibitService.deleteExhibit(id);
        return "redirect:/" + CONTROLLER_VIEW_DIR + "exhibit-list";
    }

    /* END Exhibit */
    /* -------------------------- */

        /* Event */
        @GetMapping("/event-list")
        public String eventList(Model model) {
            model.addAttribute("events", eventService.findAllEvents());
            return CONTROLLER_VIEW_DIR + "event-list";
        }
    
        @GetMapping("/event-add")
        private String eventAdd(Model model) {
            EventDto event = new EventDto();
            model.addAttribute("event", event);
            model.addAttribute("categoriesList", categoryService.findAllEventCategories());
            return CONTROLLER_VIEW_DIR + "event-add";
        }
    
        @GetMapping("/event-edit")
        public String eventEdit(@RequestParam long id, Model model) {
            EventDto event = eventService.getEventDtoById(id);
            model.addAttribute("event", event);
            model.addAttribute("categoriesList", categoryService.findAllEventCategories());
            return CONTROLLER_VIEW_DIR + "event-add";
        }
    
        @PostMapping("/event-save")
        public String eventSave(@Valid @ModelAttribute("event") EventDto event, BindingResult result, Model model) {
            if (result.hasErrors()) {
                model.addAttribute("event", event);
                model.addAttribute("categoriesList", categoryService.findAllEventCategories());
    
                return CONTROLLER_VIEW_DIR + "event-add";
            }
            
            eventService.saveEvent(event);
            return "redirect:/" + CONTROLLER_VIEW_DIR + "event-list";
        }
    
        @GetMapping("/event-delete")
        public String eventDelete(@RequestParam(name="id", required=true) long id, Model model) {
            eventService.deleteEvent(id);
            return "redirect:/" + CONTROLLER_VIEW_DIR + "event-list";
        }
    
        /* END Event */
        /* -------------------------- */

}
