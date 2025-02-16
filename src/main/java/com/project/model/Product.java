package com.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String descriptions;

    @Column(precision = 10, scale = 3)
    private BigDecimal price;

    @Column(name = "stock_quantity")
    private int stockQuantity;

    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;

    @Temporal(TemporalType.TIMESTAMP)
    private Date create_at;

    @Temporal(TemporalType.TIMESTAMP)
    private Date update_at;

}
