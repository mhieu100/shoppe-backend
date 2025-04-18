package com.project.dto;

import java.math.BigDecimal;

import com.project.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Integer id;
    private Integer quantity;
    private String productName;
    private int stockQuantity;
    private BigDecimal price;
    private String imageUrl;

    public CartItemDTO(CartItem cartItem) {
        this.id = cartItem.getId();
        this.quantity = cartItem.getQuantity();
        this.productName = cartItem.getProduct().getName();
        this.stockQuantity = cartItem.getProduct().getStockQuantity();
        this.price = cartItem.getProduct().getPrice();
        this.imageUrl = cartItem.getProduct().getImages().get(0).getImageUrl();
    }
}
