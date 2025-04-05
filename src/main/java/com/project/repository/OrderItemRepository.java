package com.project.repository;

import com.project.model.OrderItem;
import com.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    OrderItem findByOrderId(int orderId);


    @Query("SELECT o FROM OrderItem o WHERE o.product.user.email= :userEmail ")
    List<OrderItem> findByProductUser(String userEmail);

}
