package com.ecommerce.project.controller;

import com.ecommerce.project.dto.ProductDto;
import com.ecommerce.project.dto.ProductResponseDto;
import com.ecommerce.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDto> addProduct(@RequestBody ProductDto productDto, @PathVariable Long categoryId){
        ProductDto savedProductDto =productService.addProduct(categoryId, productDto);
        return new ResponseEntity<>(savedProductDto, HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponseDto> getAllProduct(){
       ProductResponseDto productResponseDto= productService.getAllProduct();
       return new ResponseEntity<>(productResponseDto,HttpStatus.OK);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponseDto> getProductByCategory(@PathVariable Long categoryId){
       ProductResponseDto productResponseDto= productService.searchByCategory(categoryId);
       return new ResponseEntity<>(productResponseDto,HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponseDto> getProductByKeyword(@PathVariable String keyword){
       ProductResponseDto productResponseDto= productService.searchProductByKeyword(keyword);
        return new ResponseEntity<>(productResponseDto,HttpStatus.FOUND);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@RequestBody ProductDto productDTO,@PathVariable Long productId){
        ProductDto updatedProductDto=productService.updateProduct(productId,productDTO);
        return new ResponseEntity<>(updatedProductDto,HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDto> deleteProduct(@PathVariable Long productId){
      ProductDto deletedProduct=  productService.deleteProduct(productId);
        return new ResponseEntity<>(deletedProduct,HttpStatus.OK);
    }
}
