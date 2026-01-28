package com.ecommerce.service;

import com.ecommerce.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);

    void deleteCategory(Long id);

    CategoryDTO getCategoryById(Long id);

    List<CategoryDTO> getAllCategories();
}
