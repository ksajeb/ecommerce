package com.ecommerce.project.controller;

import com.ecommerce.project.dto.CategoryRequestDto;
import com.ecommerce.project.dto.CategoryResponseDto;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CategoryController {

    @Autowired
    private  CategoryService categoryService;


    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponseDto> getCategories(){
        CategoryResponseDto categoryResponseDto = categoryService.getAllCategory();
        return new ResponseEntity<>(categoryResponseDto,HttpStatus.OK);
        
    }

    @PostMapping("/public/categories")
    public ResponseEntity<CategoryRequestDto> createCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto){
        CategoryRequestDto savedCategoryRequestDto1=categoryService.createCategory(categoryRequestDto);
        return new ResponseEntity<>(savedCategoryRequestDto1,HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId){
        String status=categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<String> updateCategory(@Valid @RequestBody Category category,@PathVariable Long categoryId){
        Category savedCategory=categoryService.updateCategory(category,categoryId);
        return new ResponseEntity<>("Updated Category with categoryId: "+categoryId,HttpStatus.OK);
    }
}
