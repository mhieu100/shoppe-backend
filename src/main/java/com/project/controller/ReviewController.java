package com.project.controller;


import com.project.dto.ReviewDTO;
import com.project.exception.ExistException;
import com.project.exception.NotFoundException;
import com.project.model.Review;
import com.project.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("")
    public ResponseEntity<ReviewDTO> createReview(@RequestBody Review review) throws NotFoundException, ExistException {
        ReviewDTO reviewDTO = reviewService.addReview(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewDTO);
    }

    @GetMapping("/{product_id}")
    public ResponseEntity<List<ReviewDTO>> getReview(@PathVariable("product_id") Integer product_id) {
        List<ReviewDTO> listReview = reviewService.getAllReviewsByProduct(product_id);
        return ResponseEntity.status(HttpStatus.OK).body(listReview);
    }
}
