package com.ecommerce.project.service;

import com.ecommerce.project.dto.ProductDto;
import com.ecommerce.project.dto.ProductResponseDto;
import com.ecommerce.project.exception.ApiException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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

    @Autowired
    CartRepository cartRepository;

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
    public ProductResponseDto getAllProduct(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetail= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProduct=productRepository.findAll(pageDetail);

        List<Product> products=pageProduct.getContent();

        List<ProductDto>productDtos=products.stream().map(product -> modelMapper.map(product,ProductDto.class))
                .toList();
        if(products.isEmpty()){
            throw new ApiException("No products available!!");
        }
        ProductResponseDto productResponseDto=new ProductResponseDto();
        productResponseDto.setContent(productDtos);
        productResponseDto.setPageNumber(pageProduct.getNumber());
        productResponseDto.setPageSize(pageProduct.getSize());
        productResponseDto.setTotalElements(pageProduct.getTotalElements());
        productResponseDto.setTotalPages(pageProduct.getTotalPages());
        productResponseDto.setLastPage(pageProduct.isLast());
        return productResponseDto;
    }

    @Override
    public ProductResponseDto searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category=categoryRepository.findById(categoryId).orElseThrow(()->
                new ResourceNotFoundException("Category","categoryId",categoryId));

        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetail= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProduct=productRepository.findByCategoryOrderByPriceAsc(category, pageDetail);


        List<Product> products=pageProduct.getContent();

        if(products.isEmpty()){
            throw new ApiException(category.getCategoryName()+"  category does not have any products.");
        }

        List<ProductDto>productDtos=products.stream().map(product -> modelMapper.map(product,ProductDto.class))
                .toList();
        if(products.isEmpty()){
            throw new ApiException("No products available!!");
        }
        ProductResponseDto productResponseDto=new ProductResponseDto();
        productResponseDto.setContent(productDtos);
        productResponseDto.setPageNumber(pageProduct.getNumber());
        productResponseDto.setPageSize(pageProduct.getSize());
        productResponseDto.setTotalElements(pageProduct.getTotalElements());
        productResponseDto.setTotalPages(pageProduct.getTotalPages());
        productResponseDto.setLastPage(pageProduct.isLast());
        return productResponseDto;
    }

    @Override
    public ProductResponseDto searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetail= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProduct=productRepository.findByProductNameLikeIgnoreCase("%" + keyword + "%", pageDetail);

        List<Product> products=pageProduct.getContent();
        List<ProductDto>productDtos=products.stream().map(product -> modelMapper.map(product,ProductDto.class))
                .toList();
        if(products.isEmpty()){
            throw new ApiException("Products not found with keyword: "+keyword);
        }

        ProductResponseDto productResponseDto=new ProductResponseDto();
        productResponseDto.setContent(productDtos);
        productResponseDto.setPageNumber(pageProduct.getNumber());
        productResponseDto.setPageSize(pageProduct.getSize());
        productResponseDto.setTotalElements(pageProduct.getTotalElements());
        productResponseDto.setTotalPages(pageProduct.getTotalPages());
        productResponseDto.setLastPage(pageProduct.isLast());
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
