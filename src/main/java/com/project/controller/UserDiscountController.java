package com.project.controller;

import com.project.dto.UserDiscountDTO;
import com.project.exception.ExistException;
import com.project.service.UserDiscountService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-discounts")
@RequiredArgsConstructor
public class UserDiscountController {
    private final UserDiscountService userDiscountService;

    @PostMapping()
    public ResponseEntity<UserDiscountDTO> createUserDiscount(
            @PathParam("discountCode") String discountCode) throws ExistException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userDiscountService.createUserDiscount(discountCode));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserDiscountDTO>> getUserDiscounts(
            @PathVariable int userId) throws ExistException {
        return ResponseEntity.ok(userDiscountService.getUserDiscounts(userId));
    }
}