package com.project.controller;

import com.project.dto.OrderDTO;
import com.project.dto.OrderRequestDTO;
import com.project.dto.Pagination;
import com.project.exception.ExistException;
import com.project.exception.NotAllowException;
import com.project.exception.NotFoundException;
import com.project.model.Order;
import com.project.service.OrderService;
import com.turkraft.springfilter.boot.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/order-direct/{id}")
    public ResponseEntity<OrderDTO> orderDirect(@PathVariable("id") int product_id, @RequestBody OrderRequestDTO orderRequestDTO) throws ExistException, NotAllowException {
        OrderDTO order = orderService.createDirectOrder(product_id, orderRequestDTO);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/order-from-cart")
    public ResponseEntity<OrderDTO> orderFromCart(@RequestBody OrderRequestDTO orderRequestDTO) throws ExistException {
        OrderDTO order = orderService.createOrderFromCart(orderRequestDTO);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/all")
    public ResponseEntity<Pagination> getAllOrders(@Filter Specification<Order> specification, Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrders(specification, pageable));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable int id, @RequestBody Map<String, String> requestData) throws NotFoundException {
        OrderDTO orderDTO = orderService.updateOrderStatus(id, String.valueOf(requestData.get("status")));
        return ResponseEntity.ok(orderDTO);
    }
}
