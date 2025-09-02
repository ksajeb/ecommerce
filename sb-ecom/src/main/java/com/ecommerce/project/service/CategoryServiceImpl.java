package com.ecommerce.project.service;

import com.ecommerce.project.exception.ApiException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{


    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategory() {
        List<Category> categories=categoryRepository.findAll();
        if(categories.isEmpty()){
            throw new ApiException("No category created till now.");
        }
        return categories;
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
