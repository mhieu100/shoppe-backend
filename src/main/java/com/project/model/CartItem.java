package com.project.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_item")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private ShoppingCart cart;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="product_id",referencedColumnName = "id")
    private Product product;

    private int quantity;
}

