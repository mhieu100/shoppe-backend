package com.project.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "shopping_cart")
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id",nullable = false,unique = true)
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
}
