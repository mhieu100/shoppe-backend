package com.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private String name;
    private String descriptions;
    private BigDecimal price;
    private int stockQuantity;
    private int categoryId;

    // Getters và Setters
}
