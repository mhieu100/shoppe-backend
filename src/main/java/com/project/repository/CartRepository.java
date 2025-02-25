package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.project.model.ShoppingCart;
import com.project.model.User;

@Repository
public interface CartRepository extends JpaRepository<ShoppingCart, Integer>, JpaSpecificationExecutor<ShoppingCart> {
    ShoppingCart findByUser(User user);
}
