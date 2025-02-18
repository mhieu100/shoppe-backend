package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.project.model.User;

public interface  UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>{
    User findByEmail(String email);
    User findByRefreshTokenAndEmail(String token, String email);
    boolean existsByEmail(String email);
}