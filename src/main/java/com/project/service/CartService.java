package com.project.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.project.dto.CartDTO;
import com.project.dto.CartItemDTO;
import com.project.exception.NotFoundException;
import com.project.model.CartItem;
import com.project.model.Product;
import com.project.model.ShoppingCart;
import com.project.model.User;
import com.project.repository.CartItemRepository;
import com.project.repository.CartRepository;
import com.project.repository.ProductRepository;
import com.project.repository.UserRepository;
import com.project.util.JwtUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public void addProductToCart(Integer id) {
        String email = JwtUtils.getCurrentUserLogin().isPresent() ? JwtUtils.getCurrentUserLogin().get() : "";
        User user = userRepository.findByEmail(email).get();
        Product product = productRepository.findById(id).get();
        ShoppingCart cart = cartRepository.findByUser(user);
        if (cart != null) {
            CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product);

            if (cartItem != null) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                cartItemRepository.save(cartItem);
            } else {
                CartItem newCartItem = new CartItem();
                newCartItem.setProduct(product);
                newCartItem.setQuantity(1);
                newCartItem.setCart(cart);
                cartItemRepository.save(newCartItem);
            }
            ;
        } else {
            product.setStockQuantity(product.getStockQuantity() - 1);
            productRepository.save(product);

            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUser(user);
            shoppingCart.setDate(new Date());
            cartRepository.save(shoppingCart);

            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cartItem.setCart(shoppingCart);
            cartItemRepository.save(cartItem);
        }
    }

    public CartDTO getAllProductInCartOfMe() throws NotFoundException {
        String email = JwtUtils.getCurrentUserLogin().isPresent() ? JwtUtils.getCurrentUserLogin().get() : "";
        User user = userRepository.findByEmail(email).get();
        ShoppingCart cart = cartRepository.findByUser(user);
        if (cart == null) {
            throw new NotFoundException("Cart is empty");

        }
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setUserName(user.getUsername());
        cartDTO.setDate(cart.getDate().toString());
        cartDTO.setCartItems(cart.getCartItems().stream().map(item -> {
            CartItemDTO cartItemDTO = new CartItemDTO();
            cartItemDTO.setId(item.getId());
            cartItemDTO.setProductName(item.getProduct().getName());
            cartItemDTO.setStockQuantity(item.getProduct().getStockQuantity());
            cartItemDTO.setQuantity(item.getQuantity());
            cartItemDTO.setPrice(item.getProduct().getPrice());
            cartItemDTO.setImageUrl(item.getProduct().getImages().get(0).getImageUrl());
            return cartItemDTO;
        }).toList());

        return cartDTO;
    }

    public void increaseProduct(Integer id) {
        cartItemRepository.findById(id).ifPresent(cartItem -> {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            // Product product = cartItem.getProduct();
            // product.setStockQuantity(product.getStockQuantity() - 1);
            // productRepository.save(product);
            cartItemRepository.save(cartItem);
        });
    }

    public void decreaseProduct(Integer id) {
        cartItemRepository.findById(id).ifPresent(cartItem -> {
            if (cartItem.getQuantity() == 1) {
                cartItemRepository.delete(cartItem);
            }
            cartItem.setQuantity(cartItem.getQuantity() - 1);
            // Product product = cartItem.getProduct();
            // product.setStockQuantity(product.getStockQuantity() + 1);
            // productRepository.save(product);
            cartItemRepository.save(cartItem);
        });
    }

    public void removeProduct(Integer id) {
        cartItemRepository.findById(id).ifPresent(cartItem -> {
            // Product product = cartItem.getProduct();
            // product.setStockQuantity(product.getStockQuantity() + cartItem.getQuantity());
            // productRepository.save(product);
            cartItemRepository.delete(cartItem);
        });
    }

    public void removeAllProduct() {
        String email = JwtUtils.getCurrentUserLogin().isPresent() ? JwtUtils.getCurrentUserLogin().get() : "";
        User user = userRepository.findByEmail(email).get();
        ShoppingCart cart = cartRepository.findByUser(user);
        cartItemRepository.deleteAllByCartId(cart.getId());
    }
}
