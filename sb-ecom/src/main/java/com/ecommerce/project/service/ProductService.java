package com.ecommerce.project.service;


import com.ecommerce.project.dto.ProductDto;
import com.ecommerce.project.dto.ProductResponseDto;
import com.ecommerce.project.model.Product;

public interface ProductService {
    ProductDto addProduct(Long categoryId, Product product);

    ProductResponseDto getAllProduct();

    ProductResponseDto searchByCategory(Long categoryId);

    ProductResponseDto searchProductByKeyword(String keyword);

    ProductDto updateProduct(Long productId, Product product);

    ProductDto deleteProduct(Long productId);
}
