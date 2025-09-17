package com.ecommerce.project.service;


import com.ecommerce.project.dto.ProductDto;
import com.ecommerce.project.dto.ProductResponseDto;

public interface ProductService {
    ProductDto addProduct(Long categoryId, ProductDto product);

    ProductResponseDto getAllProduct();

    ProductResponseDto searchByCategory(Long categoryId);

    ProductResponseDto searchProductByKeyword(String keyword);

    ProductDto updateProduct(Long productId, ProductDto product);

    ProductDto deleteProduct(Long productId);
}
