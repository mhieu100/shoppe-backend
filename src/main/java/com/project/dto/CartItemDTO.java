package com.project.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private Integer id;
    private Integer quantity;
    private String productName;
}
