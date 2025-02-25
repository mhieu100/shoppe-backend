package com.project.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cart_item")
@Data
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private ShoppingCart cart;

    @OneToOne
    @JoinColumn(name="product_id",referencedColumnName = "id")
    private Product product;

    private int quantity;
}

