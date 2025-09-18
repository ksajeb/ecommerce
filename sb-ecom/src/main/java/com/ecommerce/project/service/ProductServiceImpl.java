package com.ecommerce.project.service;

import com.ecommerce.project.dto.ProductDto;
import com.ecommerce.project.dto.ProductResponseDto;
import com.ecommerce.project.exception.ApiException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements  ProductService{
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductDto addProduct(Long categoryId, ProductDto productDto) {
        Category category=categoryRepository.findById(categoryId).orElseThrow(()->
                new ResourceNotFoundException("Category","categoryId",categoryId));

        boolean isProductNotPresent=true;

        List<Product> products=category.getProducts();
        for (Product value : products) {
            if (value.getProductName().equals(productDto.getProductName())) {
                isProductNotPresent = false;
                break;
            }

        }

        if (isProductNotPresent) {
            Product product = modelMapper.map(productDto, Product.class);
            product.setImage("default.png");
            product.setCategory(category);
            double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct, ProductDto.class);
        }else {
            throw new ApiException("Product already exists!!");
        }
    }

    @Override
    public ProductResponseDto getAllProduct() {
        List<Product> products=productRepository.findAll();
        List<ProductDto>productDtos=products.stream().map(product -> modelMapper.map(product,ProductDto.class))
                .toList();
        if(products.isEmpty()){
            throw new ApiException("No products available!!");
        }
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
        if(products.isEmpty()){
            throw new ApiException("No products available!!");
        }
        ProductResponseDto productResponseDto=new ProductResponseDto();
        productResponseDto.setContent(productDtos);
        return productResponseDto;
    }

    @Override
    public ProductResponseDto searchProductByKeyword(String keyword) {
        List<Product> products=productRepository.findByProductNameLikeIgnoreCase("%" + keyword + "%");
        List<ProductDto>productDtos=products.stream().map(product -> modelMapper.map(product,ProductDto.class))
                .toList();
        if(products.isEmpty()){
            throw new ApiException("No products available!!");
        }

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


    @Override
    public ProductDto updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product productFromDb=productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));

        //upload image to server
        //get the filename uploaded on server
        String path="images/";
        String fileName=fileService.uploadImage(path,image);

        //updating the new file to the product
        productFromDb.setImage(fileName);

        //save updated product
        Product updatedProduct=productRepository.save(productFromDb);

        //return dto after mapping product to dto

        return modelMapper.map(updatedProduct,ProductDto.class);
    }

}
