package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Product;
import org.springframework.data.repository.Repository;

public interface ProductRepository extends Repository<Product, Long> {
}