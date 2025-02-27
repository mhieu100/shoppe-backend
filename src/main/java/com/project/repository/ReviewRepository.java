package com.project.repository;

import com.project.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    boolean existsByUserIdAndProductId(int userId, int productId);

    List<Review> findByProductId(int productId);
}
