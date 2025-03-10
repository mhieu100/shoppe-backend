 package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.model.Discount;

public interface DiscountRepository extends JpaRepository<Discount, Integer>{
    Discount findByCode(String code);
}