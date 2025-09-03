package com.ecommerce.project.service;

import com.ecommerce.project.dto.CategoryRequestDto;
import com.ecommerce.project.dto.CategoryResponseDto;
import com.ecommerce.project.exception.ApiException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{


    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponseDto getAllCategory() {
        List<Category> categories=categoryRepository.findAll();
        if(categories.isEmpty()){
            throw new ApiException("No category created till now.");
        }
        List<CategoryRequestDto> categoryRequestDtos=categories.stream()
                .map(category -> modelMapper.map(category,CategoryRequestDto.class))
                .toList();

        CategoryResponseDto categoryResponseDto=new CategoryResponseDto();
        categoryResponseDto.setContent(categoryRequestDtos);
        return categoryResponseDto;
    }

    @Override
    public CategoryRequestDto createCategory(CategoryRequestDto categoryRequestDto) {
        Category category=modelMapper.map(categoryRequestDto,Category.class);
        Category categoryFromDb =categoryRepository.findByCategoryName(category.getCategoryName());
        if(categoryFromDb !=null){
            throw new ApiException("Category with the name "+category.getCategoryName()+" is already exist!!!");
        }
        category.setCategoryId(null);
        Category savedCategory=categoryRepository.save(category);
        return modelMapper.map(savedCategory,CategoryRequestDto.class);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category=categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","category",categoryId));
        categoryRepository.deleteById(categoryId);
        return "Category with categoryID: "+categoryId+" deleted successfully !!";
    }

    @Override
    public CategoryRequestDto updateCategory(CategoryRequestDto categoryRequestDto, Long categoryId) {
        Category savedCategory=categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","category",categoryId));
        Category category=modelMapper.map(categoryRequestDto,Category.class);
        category.setCategoryId(categoryId);
        savedCategory=categoryRepository.save(category);
        return modelMapper.map(savedCategory,CategoryRequestDto.class);
    }
}
