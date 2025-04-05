package com.project.controller;

import com.project.dto.DiscountDTO;
import com.project.exception.ExistException;
import com.project.model.Discount;
import com.project.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/discounts")
@RequiredArgsConstructor
public class DiscountController {
    private final DiscountService discountService;

    @PostMapping
    public ResponseEntity<DiscountDTO> createDiscount(@RequestBody Discount discount) throws ExistException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(discountService.createDiscount(discount));
    }

    @GetMapping("/{code}")
    public ResponseEntity<DiscountDTO> getDiscount(@PathVariable String code) throws ExistException {
        return ResponseEntity.ok(discountService.getDiscount(code));
    }

    @GetMapping
    public ResponseEntity<List<DiscountDTO>> getAllDiscounts() {
        System.out.println("call");
        return ResponseEntity.ok(discountService.getAllDiscounts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiscountDTO> updateDiscount(@PathVariable int id, @RequestBody Discount discount) throws ExistException {
        return ResponseEntity.ok(discountService.updateDiscount(id, discount));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable int id) throws ExistException {
        discountService.deleteDiscount(id);
        return ResponseEntity.noContent().build();
    }
}