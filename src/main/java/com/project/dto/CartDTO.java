package com.project.dto;

import java.util.List;

import lombok.Data;

@Data

public class CartDTO {
    private Integer id;
    private String userName;
    private String date;
    private List<CartItemDTO> cartItems;
}
