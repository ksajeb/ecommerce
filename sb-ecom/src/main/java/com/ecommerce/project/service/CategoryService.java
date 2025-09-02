package com.ecommerce.project.service;

import com.ecommerce.project.dto.CategoryRequestDto;
import com.ecommerce.project.dto.CategoryResponseDto;
import com.ecommerce.project.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto getAllCategory();

    CategoryRequestDto createCategory(CategoryRequestDto categoryRequestDto);

    String deleteCategory(Long categoryId);

    Category updateCategory(Category category, Long categoryId);
}
