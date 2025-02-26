package com.project.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.project.model.ShoppingCart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    private Integer id;
    private String userName;
    private String date;
    private List<CartItemDTO> cartItems;

    public CartDTO(ShoppingCart cart) {
        this.id = cart.getId();
        this.userName = cart.getUser().getEmail();
        this.date = cart.getDate().toString();
        this.cartItems = cart.getCartItems().stream().map(CartItemDTO::new).collect(Collectors.toList());
    }
}
