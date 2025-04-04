package com.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "user_discount")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDiscount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Discount discount;

    @Temporal(TemporalType.TIMESTAMP)
    private Date discountDate;

    private boolean used;
}
