package com.project.dto;

import com.project.model.Discount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountDTO {
    private int id;
    private String code;
    private int usage_limit;
    private int used_count;
    private Double value;

    public DiscountDTO(Discount discount) {
        this.id = discount.getId();
        this.code = discount.getCode();
        this.usage_limit = discount.getUsageLimit();
        this.used_count = discount.getUsedCount();
        this.value = discount.getValue();
    }
}
