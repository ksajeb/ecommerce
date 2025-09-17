package com.ecommerce.project.service;

import com.ecommerce.project.dto.ProductDto;
import com.ecommerce.project.dto.ProductResponseDto;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements  ProductService{
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ProductDto addProduct(Long categoryId, ProductDto productDto) {
        Category category=categoryRepository.findById(categoryId).orElseThrow(()->
                new ResourceNotFoundException("Category","categoryId",categoryId));
        Product product=modelMapper.map(productDto,Product.class);
        product.setImage("default.png");
        product.setCategory(category);
        double specialPrice=product.getPrice()-((product.getDiscount()*0.01)* product.getPrice());
        product.setSpecialPrice(specialPrice);
        Product savedProduct=productRepository.save(product);
        return modelMapper.map(savedProduct,ProductDto.class);
    }

    @Override
    public ProductResponseDto getAllProduct() {
        List<Product> products=productRepository.findAll();
        List<ProductDto>productDtos=products.stream().map(product -> modelMapper.map(product,ProductDto.class))
                .toList();
        ProductResponseDto productResponseDto=new ProductResponseDto();
        productResponseDto.setContent(productDtos);
        return productResponseDto;
    }

    @Override
    public ProductResponseDto searchByCategory(Long categoryId) {
        Category category=categoryRepository.findById(categoryId).orElseThrow(()->
                new ResourceNotFoundException("Category","categoryId",categoryId));

        List<Product> products=productRepository.findByCategoryOrderByPriceAsc(category);
        List<ProductDto>productDtos=products.stream().map(product -> modelMapper.map(product,ProductDto.class))
                .toList();
        ProductResponseDto productResponseDto=new ProductResponseDto();
        productResponseDto.setContent(productDtos);
        return productResponseDto;
    }

    @Override
    public ProductResponseDto searchProductByKeyword(String keyword) {
        List<Product> products=productRepository.findByProductNameLikeIgnoreCase("%" + keyword + "%");
        List<ProductDto>productDtos=products.stream().map(product -> modelMapper.map(product,ProductDto.class))
                .toList();
        ProductResponseDto productResponseDto=new ProductResponseDto();
        productResponseDto.setContent(productDtos);
        return productResponseDto;
    }

    @Override
    public ProductDto updateProduct(Long productId, ProductDto productDTO) {
        Product productFromDb=productRepository.findById(productId).
                orElseThrow(()->new ResourceNotFoundException("Product","product",productId));
        Product product=modelMapper.map(productDTO,Product.class);
        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setPrice(product.getPrice());
        productFromDb.setDiscount(product.getDiscount());
        productFromDb.setSpecialPrice(product.getSpecialPrice());

        Product savedProduct=productRepository.save(productFromDb);

        return modelMapper.map(savedProduct,ProductDto.class);
    }

    @Override
    public ProductDto deleteProduct(Long productId) {
        Product product=productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));
        productRepository.delete(product);
        return modelMapper.map(product,ProductDto.class);
    }
}
