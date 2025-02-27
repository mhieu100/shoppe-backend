package com.project.service;

import com.project.dto.ReviewDTO;
import com.project.enums.OrderStatus;
import com.project.exception.ExistException;
import com.project.exception.NotFoundException;
import com.project.model.Product;
import com.project.model.Review;
import com.project.model.User;
import com.project.repository.OrderRepository;
import com.project.repository.ProductRepository;
import com.project.repository.ReviewRepository;
import com.project.repository.UserRepository;
import com.project.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;


    public boolean canUserReviewProduct(int userId, int productId) throws NotFoundException {
        String email = JwtUtils.getCurrentUserLogin().isPresent() ? JwtUtils.getCurrentUserLogin().get() : null;
        User user = userRepository.findByEmail(email).get();
        if (user == null) {
            throw new NotFoundException("Vui lòng đăng nhập");
        }

        return orderRepository.existsByUserIdAndProductIdAndStatus(userId, productId, OrderStatus.DELIVERED);
    }

    public ReviewDTO addReview(Review review) throws NotFoundException, ExistException {

        String email = JwtUtils.getCurrentUserLogin().isPresent() ? JwtUtils.getCurrentUserLogin().get() : null;
        User user = userRepository.findByEmail(email).get();
        if (user == null) {
            throw new NotFoundException("Please login first");
        }

        if (!canUserReviewProduct(user.getId(), review.getProduct().getId())) {
            throw new IllegalStateException("you do not have permission to add review");
        }

        boolean hasReviewed = reviewRepository.existsByUserIdAndProductId(user.getId(), review.getProduct().getId());
        if (hasReviewed) {
            throw new IllegalStateException("You commented, don't have permission to add review");
        }

        Product product = productRepository.findById(review.getProduct().getId()).get();

        review.setProduct(product);
        review.setUser(user);
        review.setCreated_at(new Date());
        Review savedReview = reviewRepository.save(review);

        return new ReviewDTO(savedReview);
    }

    public List<ReviewDTO> getAllReviewsByProduct(int productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);

        return reviews.stream().map(ReviewDTO::new).collect(Collectors.toList());
    }

    public void deleteReview(int reviewId) throws NotFoundException {

    }
}
