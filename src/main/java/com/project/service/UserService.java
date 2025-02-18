package com.project.service;

import com.project.enums.Role;
import com.project.exception.AlreadyEmailExistException;
import com.project.model.User;
import com.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User user){
        if(userRepository.existsByEmail(user.getEmail())){
            throw  new AlreadyEmailExistException("email already exist! Please try again");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(user.getRoles() ==null || user.getRoles().isEmpty()){
            user.setRoles(Collections.singleton(Role.CUSTOMER));
        }
        return userRepository.save(user);
    }
}
