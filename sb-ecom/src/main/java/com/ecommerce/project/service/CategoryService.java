package com.ecommerce.project.service;

import com.ecommerce.project.dto.CategoryRequestDto;
import com.ecommerce.project.dto.CategoryResponseDto;


public interface CategoryService {
    CategoryResponseDto getAllCategory();

    CategoryRequestDto createCategory(CategoryRequestDto categoryRequestDto);

    CategoryRequestDto deleteCategory(Long categoryId);

    CategoryRequestDto updateCategory(CategoryRequestDto categoryRequestDto, Long categoryId);
}
