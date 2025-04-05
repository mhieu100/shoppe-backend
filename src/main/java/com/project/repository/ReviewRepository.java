package com.project.repository;

import com.project.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    boolean existsByUserIdAndProductId(int userId, int productId);

    List<Review> findByProductId(int productId);

    @Query("SELECT r FROM Review r JOIN r.product p WHERE p.user.id = :sellerId")
    List<Review> findAllReviewsBySellerId(@Param("sellerId") int sellerId);
}
