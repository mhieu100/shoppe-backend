package com.project.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.dto.Pagination;
import com.project.dto.UserDTO;
import com.project.exception.ExistException;
import com.project.exception.NotFoundException;
import com.project.model.User;
import com.project.service.UserService;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Pagination> getAllUsers(@Filter Specification<User> specification,
            Pageable pageable) {
        return ResponseEntity.ok().body(userService.getAllUsers(specification, pageable));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody User user) throws ExistException {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody User user)
            throws NotFoundException {
        return ResponseEntity.ok().body(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) throws NotFoundException {
        userService.deleteUser(id);
    }
}