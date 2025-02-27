package com.project.dto;


import com.project.model.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private int id;
    private String customerName;
    private String productName;
    private int rating;
    private String comment;
    private Date reviewDate;

    public ReviewDTO(Review review) {
        this.customerName = review.getUser().getFullname();
        this.rating = review.getRating();
        this.productName = review.getProduct().getName();
        this.comment = review.getComment();
        this.reviewDate = review.getCreated_at();
    }
}
