package com.project.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.dto.CartDTO;
import com.project.exception.NotFoundException;
import com.project.service.CartService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/{id}")
    public ResponseEntity<String> addProductToCart(@PathVariable Integer id) {
        cartService.addProductToCart(id);
        return ResponseEntity.ok("Product added to cart");
    }

    @GetMapping
    public ResponseEntity<CartDTO> getAllProductInCartOfMe() throws NotFoundException {
        return ResponseEntity.ok().body(cartService.getAllProductInCartOfMe());
    }
    
    @PostMapping("/up/{id}")
    public ResponseEntity<String> increaseProduct(@PathVariable Integer id) {
        cartService.increaseProduct(id);
        return ResponseEntity.ok("Increase quantity of product in cart");
    }
    
    @PostMapping("/down/{id}")
    public ResponseEntity<String> decreaseProduct(@PathVariable Integer id) {
        cartService.decreaseProduct(id);
        return ResponseEntity.ok("Decrease quantity of product in cart");
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> removeProduct(@PathVariable Integer id) {
        cartService.removeProduct(id);
        return ResponseEntity.ok("Product removed from cart");
    }


    @DeleteMapping("/remove-all")
    public ResponseEntity<String> removeAllProduct() {
        cartService.removeAllProduct();
        return ResponseEntity.ok("Product removed All from cart");
    }
}
