package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.project.model.CartItem;
import com.project.model.Product;
import com.project.model.ShoppingCart;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer>, JpaSpecificationExecutor<CartItem> {
    CartItem findByCartAndProduct(ShoppingCart cart, Product product);
}
