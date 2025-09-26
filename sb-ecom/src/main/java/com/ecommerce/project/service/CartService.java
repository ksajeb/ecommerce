package com.ecommerce.project.service;

import com.ecommerce.project.dto.CartDTO;
import org.springframework.stereotype.Service;


public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);
}
