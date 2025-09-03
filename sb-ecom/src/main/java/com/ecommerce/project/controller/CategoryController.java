package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstant;
import com.ecommerce.project.dto.CategoryRequestDto;
import com.ecommerce.project.dto.CategoryResponseDto;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
public class CategoryController {

    @Autowired
    private  CategoryService categoryService;


    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponseDto> getCategories(
            @RequestParam(name="pageNumber",defaultValue = AppConstant.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(name="pageSize",defaultValue = AppConstant.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstant.SORT_CATEGORY_BY,required = false)String sortBy,
            @RequestParam(name = "sortOrder",defaultValue = AppConstant.SORT_DIR,required = false)String sortOrder
    ){
        CategoryResponseDto categoryResponseDto = categoryService.getAllCategory(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(categoryResponseDto,HttpStatus.OK);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<CategoryRequestDto> createCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto){
        CategoryRequestDto savedCategoryRequestDto=categoryService.createCategory(categoryRequestDto);
        return new ResponseEntity<>(savedCategoryRequestDto,HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryRequestDto> deleteCategory(@PathVariable Long categoryId){
        CategoryRequestDto deletedCategory=categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(deletedCategory, HttpStatus.OK);
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryRequestDto> updateCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto,@PathVariable Long categoryId){
        CategoryRequestDto savedCategoryDTO=categoryService.updateCategory(categoryRequestDto,categoryId);
        return new ResponseEntity<>(savedCategoryDTO,HttpStatus.OK);
    }
}
