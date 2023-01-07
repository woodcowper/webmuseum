package com.webmuseum.museum.service;

import java.util.List;
import java.util.Optional;

import com.webmuseum.museum.dto.CategoryDto;
import com.webmuseum.museum.entity.Category;
import com.webmuseum.museum.models.ECategoryType;

public interface ICategoryService {

    List<Category> findAllEventCategoriesWithIds(List<Long> ids);

    List<Category> findAllExhibitCategoriesWithIds(List<Long> ids);

    List<CategoryDto> findAllEventCategories();

    List<CategoryDto> findAllExhibitCategories();

    Optional<Category> getCategoryById(long id);

    CategoryDto getCategoryDtoById(long id, long languageId);

    CategoryDto getCategoryDtoById(long id);

    String getCategoryTypeNameById(long id);

    String getFormattedCategoryTypeName(Category category);

    String getFormattedCategoryTypeName(CategoryDto category);

    void deleteCategory(long id);

    void saveCategory(Category category);

    void saveCategory(CategoryDto categoryDto);

    boolean checkIfExistsOthers(Long categoryId, String name, ECategoryType type, long languageId);
    
}
