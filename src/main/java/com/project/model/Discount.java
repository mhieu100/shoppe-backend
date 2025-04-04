package com.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "discount")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String code;

    @Column(name = "usage_limit")
    private int usageLimit;

    @Column(name = "used_count")
    private int usedCount;

    //    @Column(precision = 10, scale = 2)
    private Double value;
}
