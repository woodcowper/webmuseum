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
    public List<Category> findAllEventCategoriesWithIds(List<Long> ids){
        return findAllWithIds(ids, CategoryType.EVENT);
    }

    @Override
    public List<Category> findAllExhibitCategoriesWithIds(List<Long> ids){
        return findAllWithIds(ids, CategoryType.EXHIBIT);
    }

    @Override
    public List<CategoryDto> findAllEventCategories() {
        return findAllCategoriesDtoByType(CategoryType.EVENT);
    }

    @Override
    public List<CategoryDto> findAllExhibitCategories() {
        return findAllCategoriesDtoByType(CategoryType.EXHIBIT);
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
    public boolean checkIfExistsOthers(Long categoryId, String name, CategoryType type) {
        if(categoryId != null){
            return findAllCategoriesByType(type).stream()
            .filter(category -> category.getName().equals(name))
            .filter(category -> category.getId() != categoryId)
            .count() > 0;
        }
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

    private List<Category> findAllCategoriesByType(CategoryType type) {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .filter(category -> category.getType() == type)
                .sorted((category1, category2) -> category1.getName().compareTo(category2.getName()))
                .collect(Collectors.toList());
    }

    private List<CategoryDto> findAllCategoriesDtoByType(CategoryType type) {
        return findAllCategoriesByType(type).stream()
                .map((category) -> mapToCategoryDto(category))
                .collect(Collectors.toList());
    }

    private List<Category> findAllWithIds(List<Long> ids, CategoryType type){
        return findAllCategoriesByType(type).stream()
            .filter((category) -> ids.contains(category.getId()))
            .toList();
    }
    
}
