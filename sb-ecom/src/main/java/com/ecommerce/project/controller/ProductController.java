package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstant;
import com.ecommerce.project.dto.ProductDto;
import com.ecommerce.project.dto.ProductResponseDto;
import com.ecommerce.project.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDto> addProduct(@Valid @RequestBody ProductDto productDto, @PathVariable Long categoryId){
        ProductDto savedProductDto =productService.addProduct(categoryId, productDto);
        return new ResponseEntity<>(savedProductDto, HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponseDto> getAllProduct(
            @RequestParam(name="pageNumber",defaultValue = AppConstant.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(name="pageSize",defaultValue = AppConstant.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstant.SORT_PRODUCT_BY,required = false)String sortBy,
            @RequestParam(name = "sortOrder",defaultValue = AppConstant.SORT_DIR,required = false)String sortOrder
    ){
       ProductResponseDto productResponseDto= productService.getAllProduct(pageNumber,pageSize,sortBy,sortOrder);
       return new ResponseEntity<>(productResponseDto,HttpStatus.OK);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponseDto> getProductByCategory(@PathVariable Long categoryId,
                                                                   @RequestParam(name="pageNumber",defaultValue = AppConstant.PAGE_NUMBER,required = false) Integer pageNumber,
                                                                   @RequestParam(name="pageSize",defaultValue = AppConstant.PAGE_SIZE,required = false) Integer pageSize,
                                                                   @RequestParam(name = "sortBy",defaultValue = AppConstant.SORT_PRODUCT_BY,required = false)String sortBy,
                                                                   @RequestParam(name = "sortOrder",defaultValue = AppConstant.SORT_DIR,required = false)String sortOrder){
       ProductResponseDto productResponseDto= productService.searchByCategory(categoryId,pageNumber,pageSize,sortBy,sortOrder);
       return new ResponseEntity<>(productResponseDto,HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponseDto> getProductByKeyword(@PathVariable String keyword,
                                                                  @RequestParam(name="pageNumber",defaultValue = AppConstant.PAGE_NUMBER,required = false) Integer pageNumber,
                                                                  @RequestParam(name="pageSize",defaultValue = AppConstant.PAGE_SIZE,required = false) Integer pageSize,
                                                                  @RequestParam(name = "sortBy",defaultValue = AppConstant.SORT_PRODUCT_BY,required = false)String sortBy,
                                                                  @RequestParam(name = "sortOrder",defaultValue = AppConstant.SORT_DIR,required = false)String sortOrder){
       ProductResponseDto productResponseDto= productService.searchProductByKeyword(keyword,pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponseDto,HttpStatus.FOUND);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@Valid @RequestBody ProductDto productDTO,@PathVariable Long productId){
        ProductDto updatedProductDto=productService.updateProduct(productId,productDTO);
        return new ResponseEntity<>(updatedProductDto,HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDto> deleteProduct(@PathVariable Long productId){
      ProductDto deletedProduct=  productService.deleteProduct(productId);
        return new ResponseEntity<>(deletedProduct,HttpStatus.OK);
    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDto> uploadImage(@PathVariable Long productId,
                                                  @RequestParam("image")MultipartFile image) throws IOException {
        ProductDto updatedProduct= productService.updateProductImage(productId,image);
        return new ResponseEntity<>(updatedProduct,HttpStatus.OK);
    }
}
