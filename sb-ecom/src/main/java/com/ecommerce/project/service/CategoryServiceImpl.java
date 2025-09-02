package com.ecommerce.project.service;

import com.ecommerce.project.exception.ApiException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{

//    private List<Category> categories=new ArrayList<>();
//    private Long nextId=1L;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public void createCategory(Category category) {
        Category savedCategory=categoryRepository.findByCategoryName(category.getCategoryName());
        if(savedCategory!=null){
            throw new ApiException("Category with the name "+category.getCategoryName()+" is already exist!!!");
        }
        category.setCategoryId(null);
        categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category=categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","category",categoryId));
        categoryRepository.deleteById(categoryId);
        return "Category with categoryID: "+categoryId+" deleted successfully !!";
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        Category existingCategory=categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","category",categoryId));
        category.setCategoryId(categoryId);
        existingCategory.setCategoryName(category.getCategoryName());
        return categoryRepository.save(existingCategory);
    }
}
