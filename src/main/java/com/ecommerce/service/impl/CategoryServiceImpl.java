package com.ecommerce.service.impl;

import com.ecommerce.dto.CategoryDTO;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Category;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private static final String CATEGORY_RESOURCE = "Category";
    private static final String CATEGORY_ALREADY_EXISTS_MSG = "Category with name '%s' already exists";
    private static final String CATEGORY_WITH_PRODUCTS_MSG = "Cannot delete category with existing products";

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        log.info("Creating category: {}", categoryDTO.getName());

        if (categoryRepository.existsByNameIgnoreCase(categoryDTO.getName())) {
            throw new BadRequestException(String.format(CATEGORY_ALREADY_EXISTS_MSG, categoryDTO.getName()));
        }

        Category category = modelMapper.map(categoryDTO, Category.class);
        Category savedCategory = categoryRepository.save(category);

        log.info("Category created successfully with id: {}", savedCategory.getId());
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        log.info("Updating category with id: {}", id);

        Category category = getCategoryOrThrow(id);

        if (!category.getName().equalsIgnoreCase(categoryDTO.getName()) &&
            categoryRepository.existsByNameIgnoreCase(categoryDTO.getName())) {
            throw new BadRequestException(String.format(CATEGORY_ALREADY_EXISTS_MSG, categoryDTO.getName()));
        }

        modelMapper.map(categoryDTO, category);
        Category updatedCategory = categoryRepository.save(category);

        log.info("Category updated successfully with id: {}", id);
        return modelMapper.map(updatedCategory, CategoryDTO.class);
    }

    @Override
    public void deleteCategory(Long id) {
        log.info("Deleting category with id: {}", id);

        Category category = getCategoryOrThrow(id);

        if (hasCategoryProducts(category)) {
            throw new BadRequestException(CATEGORY_WITH_PRODUCTS_MSG);
        }

        categoryRepository.delete(category);
        log.info("Category deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        log.info("Fetching category with id: {}", id);
        Category category = getCategoryOrThrow(id);
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        log.info("Fetching all categories");
        return categoryRepository.findAll().stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a category by ID or throws ResourceNotFoundException if not found.
     *
     * @param id the category ID
     * @return the Category entity
     * @throws ResourceNotFoundException if category is not found
     */
    private Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.withId(CATEGORY_RESOURCE, id));
    }

    /**
     * Checks if a category has associated products.
     *
     * @param category the category to check
     * @return true if category has products, false otherwise
     */
    private boolean hasCategoryProducts(Category category) {
        return category.getProducts() != null && !category.getProducts().isEmpty();
    }
}
