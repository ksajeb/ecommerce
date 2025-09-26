package com.ecommerce.project.service;

import com.ecommerce.project.dto.CartDTO;
import com.ecommerce.project.dto.ProductDto;
import com.ecommerce.project.exception.ApiException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.repositories.CartItemRepository;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.ProductRepository;
import com.ecommerce.project.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService{
    @Autowired
    CartRepository cartRepository;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        //find existing cart or create new
        Cart cart=createCart();

        //retrieve product detail
        //perform validation
        Product product=productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));
        CartItem cartItem=cartItemRepository.findCartItemByCartIdAndProductId(
                cart.getCartId(),
                productId
        );
        if(cartItem!=null){
            throw  new ApiException("Product"+product.getProductName()+" is already exist in the cart!!");
        }
        if(product.getQuantity()==0){
            throw new ApiException(product.getProductName()+" is not available!");
        }
        if(product.getQuantity()<quantity){
            throw new ApiException("Please , make an order of the "+product.getProductName()+
                    " is less than or equal to quantity!!"+product.getQuantity());
        }
        //create cart item
        CartItem newCartItem=new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setCart(cart);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        //save cart item
        cartItemRepository.save(newCartItem);
        product.setQuantity(product.getQuantity());
        cart.setTotalPrice(cart.getTotalPrice()+(product.getSpecialPrice()*quantity));

        cartRepository.save(cart);

        CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);

        List<CartItem> cartItems=cart.getCartItems();
        Stream<ProductDto> productDtoStream=cartItems.stream().map(item->{
            ProductDto map=modelMapper.map(item.getProduct(),ProductDto.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productDtoStream.toList());
        //return updated cart
        return  cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts=cartRepository.findAll();
        if(carts.isEmpty()){
            throw new ApiException("No carts exists!!");
        }

        List<CartDTO> cartDTOS=carts.stream().map(
                cart -> {
                    CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);
                    List<ProductDto> products=cart.getCartItems().stream()
                            .map(p->modelMapper.map(p.getProduct(),ProductDto.class))
                            .collect(Collectors.toList());
                    cartDTO.setProducts(products);
                    return cartDTO;
                }).toList();
        return cartDTOS;
    }


    private Cart createCart(){
    Cart userCart=cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart!=null){
        return userCart;
    }
        Cart cart=new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        return cartRepository.save(cart);

    }

}
