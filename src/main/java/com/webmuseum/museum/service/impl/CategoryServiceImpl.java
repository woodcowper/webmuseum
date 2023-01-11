package com.webmuseum.museum.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webmuseum.museum.dto.CategoryDto;
import com.webmuseum.museum.entity.Category;
import com.webmuseum.museum.entity.CategoryDescription;
import com.webmuseum.museum.entity.User;
import com.webmuseum.museum.models.ECategoryType;
import com.webmuseum.museum.repository.CategoryRepository;
import com.webmuseum.museum.service.ICategoryService;
import com.webmuseum.museum.service.ILanguageService;
import com.webmuseum.museum.service.IUserService;
import com.webmuseum.museum.utils.LanguageHelper;

@Service
public class CategoryServiceImpl implements ICategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ILanguageService languageService;

    @Autowired
    private IUserService userService;

    @Override
    public List<Category> findAllEventCategoriesWithIds(List<Long> ids){
        return findAllWithIds(ids, ECategoryType.EVENT);
    }

    @Override
    public List<Category> findAllExhibitCategoriesWithIds(List<Long> ids){
        return findAllWithIds(ids, ECategoryType.EXHIBIT);
    }

    @Override
    public List<CategoryDto> findAllEventCategoriesWithoutIds(List<Long> ids){
        return  findAllCategoriesByType(ECategoryType.EVENT).stream()
            .filter((category) -> !ids.contains(category.getId()))
            .map((cat) -> mapToCategoryDto(cat, LanguageHelper.DEFAULS_LANGUAGE_ID))
            .sorted((cat1, cat2) -> cat1.getName().compareTo(cat2.getName()))
            .toList();
    }

    

    @Override
    public List<CategoryDto> findAllEventCategoriesForCurUser(){
        User curUser = userService.getCurrentUser();
        return curUser.getSubscribedCategoryEvents().stream()
            .sorted((cat1, cat2) -> getName(cat1, LanguageHelper.DEFAULS_LANGUAGE_ID).compareTo(getName(cat2, LanguageHelper.DEFAULS_LANGUAGE_ID)))
            .map((cat) -> mapToCategoryDto(cat, LanguageHelper.DEFAULS_LANGUAGE_ID))
            .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDto> findAllEventCategories() {
        return findAllCategoriesDtoByType(ECategoryType.EVENT);
    }

    @Override
    public List<CategoryDto> findAllExhibitCategories() {
        return findAllCategoriesDtoByType(ECategoryType.EXHIBIT);
    }

    @Override
    public String getCategoryTypeNameById(long id){
        Optional<Category> category = getCategoryById(id);
        if(!category.isPresent()){
            return "";
        }
        return getFormattedCategoryTypeName(getCategoryById(id).get());
    }

    @Override
    public String getFormattedCategoryTypeName(Category category){
        return category.getType().name().toLowerCase();
    }

    @Override
    public String getFormattedCategoryTypeName(CategoryDto category){
        return category.getType().name().toLowerCase();
    }

    @Override
    public Optional<Category> getCategoryById(long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public CategoryDto getCategoryDtoById(long id, long languageId) {
        return mapToCategoryDto(categoryRepository.findById(id).get(), languageId);
    }

    @Override
    public CategoryDto getCategoryDtoById(long id) {
        return mapToCategoryDto(categoryRepository.findById(id).get(), LanguageHelper.DEFAULS_LANGUAGE_ID);
    }

    @Override
    public void deleteCategory(long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            categoryRepository.delete(category.get());
        }
    }

    @Override
    public void saveCategory(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public void saveCategory(CategoryDto categoryDto) {
        saveCategory(mapToCategory(categoryDto));
    }

    @Override
    public boolean checkIfExistsOthers(Long categoryId, String name, ECategoryType type, long languageId) {
        if(categoryId != null){
            return findAllCategoriesByType(type).stream()
                .filter(category -> getName(category, languageId).equals(name))
                .filter(category -> category.getId() != categoryId)
                .count() > 0;
        }
        return findAllCategoriesByType(type).stream()
            .filter(category ->  getName(category, languageId).equals(name))
            .count() > 0;
    }

    @Override
    public boolean subscribeCategory(User user, long categorytId){
        if (user.getSubscribedCategoryEvents().stream().anyMatch((cat) -> cat.getId() == categorytId)) {
            return true;
        }
        Optional<Category> optCategory = getCategoryById(categorytId);
        if (!optCategory.isPresent()) {
            return false;
        }
        Category category = optCategory.get();
        category.getSubscribers().add(user);
        user.getSubscribedCategoryEvents().add(category);
        userService.saveUser(user);
        return true;
    }

    @Override
    public boolean unsubscribeCategory(User user, long categorytId){
        if (!user.getSubscribedCategoryEvents().stream().anyMatch((cat) -> cat.getId() == categorytId)) {
            return true;
        }
        Optional<Category> optCategory = getCategoryById(categorytId);
        if (!optCategory.isPresent()) {
            return false;
        }
        Category category = optCategory.get();
        category.getSubscribers().remove(user);
        user.getSubscribedCategoryEvents().remove(category);
        userService.saveUser(user);
        return true;
    }

    private CategoryDto mapToCategoryDto(Category category, long languageId){
        CategoryDto categoryDto = new CategoryDto(category.getType(), languageId);
        categoryDto.setId(category.getId());
        categoryDto.setName(getName(category, languageId));
        return categoryDto;
    }

    private Category mapToCategory(CategoryDto categoryDto){
        Category category;
        if(categoryDto.getId() == null){
            category = new Category();
        } else {
            category = getCategoryById(categoryDto.getId()).get();
        }
        CategoryDescription categoryDescription = getDescription(category, categoryDto.getLanguageId());
        if(categoryDescription == null){
            categoryDescription = new CategoryDescription();
            categoryDescription.setLanguage(languageService.getLanguageById(categoryDto.getLanguageId()).get());
            categoryDescription.setCategory(category);
            category.getDescriptions().add(categoryDescription);
        }
        categoryDescription.setName(categoryDto.getName());
        category.setType(categoryDto.getType());
        return category;
    }

    private List<Category> findAllCategoriesByType(ECategoryType type) {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .filter(category -> category.getType() == type)
                .sorted((category1, category2) -> getName(category1, LanguageHelper.DEFAULS_LANGUAGE_ID).compareTo(getName(category2, LanguageHelper.DEFAULS_LANGUAGE_ID)))
                .collect(Collectors.toList());
    }

    private List<CategoryDto> findAllCategoriesDtoByType(ECategoryType type, long languageId) {
        return findAllCategoriesByType(type).stream()
                .map((category) -> mapToCategoryDto(category, languageId))
                .sorted((category1, category2) -> category1.getName().compareTo(category2.getName()))
                .collect(Collectors.toList());
    }

    private List<CategoryDto> findAllCategoriesDtoByType(ECategoryType type) {
        return findAllCategoriesDtoByType(type, LanguageHelper.DEFAULS_LANGUAGE_ID);
    }

    private List<Category> findAllWithIds(List<Long> ids, ECategoryType type){
        return findAllCategoriesByType(type).stream()
            .filter((category) -> ids.contains(category.getId()))
            .toList();
    }

    @Override
    public CategoryDescription getDescription(Category category, long languageId) {
        Optional<CategoryDescription> description = category.getDescriptions().stream()
                .filter((desc) -> desc.getLanguage().getId() == languageId)
                .findFirst();
        if (description.isPresent()) {
            return description.get();
        }
        return null;
    }

    private String getName(Category category, long languageId){
        CategoryDescription categoryDescription =  getDescription(category, languageId);
        if(categoryDescription == null){
            return "";
        }
        return categoryDescription.getName();
    }

}
