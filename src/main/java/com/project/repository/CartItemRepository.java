package com.project.repository;

import com.project.model.CartItem;
import com.project.model.Product;
import com.project.model.ShoppingCart;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByCartId(int cart_id);

    CartItem findByCartAndProduct(ShoppingCart cart, Product product);

    void deleteByCartId(int id);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.cart.id = :cartId")
    @Transactional
    void deleteAllByCartId(int cartId);
}
