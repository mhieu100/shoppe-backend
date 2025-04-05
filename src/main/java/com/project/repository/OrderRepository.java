package com.project.repository;

import com.project.enums.OrderStatus;
import com.project.model.Order;
import com.project.model.User;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Page<Order> findAll(Specification<Order> specification, Pageable pageable);

    List<Order> findByUser(User user);

    @Query("SELECT COUNT(o) > 0 FROM OrderItem o WHERE o.product.user.id = :userId AND o.product.id = :productId AND o.status = :status")
    boolean existsByUserIdAndProductIdAndStatus(@Param("userId") int userId, @Param("productId") int productId, @Param("status") OrderStatus status);
}
