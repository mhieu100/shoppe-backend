package com.project.repository;

import com.project.model.User;
import com.project.model.UserDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDiscountRepository extends JpaRepository<UserDiscount, Integer> {
    List<UserDiscount> findByUserId(int userId);

    Optional<UserDiscount> findByUserAndDiscountCode(User user, String code);

}