package com.project.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "product_images")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "is_primary")
    private Boolean isPrimary;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created_at;

}