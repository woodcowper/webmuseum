package com.webmuseum.museum.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webmuseum.museum.dto.CategoryDto;
import com.webmuseum.museum.entity.Category;
import com.webmuseum.museum.entity.CategoryType;
import com.webmuseum.museum.repository.CategoryRepository;
import com.webmuseum.museum.service.ICategoryService;

@Service
public class CategoryServiceImpl implements ICategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> findAllEventCategories() {
        return findAllCategoriesByType(CategoryType.EVENT);
    }

    @Override
    public List<CategoryDto> findAllExhibitCategories() {
        return findAllCategoriesByType(CategoryType.EXHIBIT);
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
    public CategoryDto getCategoryDtoById(long id) {
        return mapToCategoryDto(categoryRepository.findById(id).get());
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
        categoryRepository.save(mapToCategory(categoryDto));
    }

    @Override
    public boolean checkIfExists(String name, CategoryType type) {
        return findAllCategoriesByType(type).stream()
            .filter(category -> category.getName().equals(name))
            .count() > 0;
    }

    private CategoryDto mapToCategoryDto(Category category){
        CategoryDto categoryDto = new CategoryDto(category.getType());
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        return categoryDto;
    }

    private Category mapToCategory(CategoryDto categoryDto){
        Category category;
        if(categoryDto.getId() == null){
            category = new Category();
        } else {
            category = getCategoryById(categoryDto.getId()).get();
        }
        category.setName(categoryDto.getName());
        category.setType(categoryDto.getType());

        return category;
    }

    public List<CategoryDto> findAllCategoriesByType(CategoryType type) {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .filter(category -> category.getType() == type)
                .sorted((category1, category2) -> category1.getName().compareTo(category2.getName()))
                .map((category) -> mapToCategoryDto(category))
                .collect(Collectors.toList());
    }
    
}
