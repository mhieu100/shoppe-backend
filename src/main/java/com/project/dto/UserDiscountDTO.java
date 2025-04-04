package com.project.dto;

import com.project.model.UserDiscount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDiscountDTO {
    private int id;
    private int discountId;
    private int userId;
    private Date discountDate;

    public UserDiscountDTO(UserDiscount userDiscount) {
        this.id = userDiscount.getId();
        this.discountId = userDiscount.getDiscount().getId();
        this.userId = userDiscount.getUser().getId();
        this.discountDate = userDiscount.getDiscountDate();
    }
}