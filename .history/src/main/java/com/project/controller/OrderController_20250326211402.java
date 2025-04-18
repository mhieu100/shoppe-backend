package com.project.controller;

import com.project.dto.OrderDTO;
import com.project.dto.OrderItemDTO;
import com.project.dto.OrderRequestDTO;
import com.project.dto.Pagination;
import com.project.enums.OrderStatus;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable int orderId) {
        OrderDTO order = orderService.getOrder(orderId);
        return ResponseEntity.ok(order);
    }

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

    @PutMapping("/item/{itemId}/status")
    public ResponseEntity<OrderItemDTO> updateOrderStatus(@PathVariable int itemId, @RequestBody Map<String, String> requestData) throws NotFoundException {
        OrderItemDTO orderItemDTO = orderService.updateItemStatus(itemId, OrderStatus.valueOf(String.valueOf(requestData.get("status"))));
        return ResponseEntity.ok(orderItemDTO);
    }

    @GetMapping
    public ResponseEntity<List<OrderItemDTO>> getOrderOfMe() {
        return ResponseEntity.ok(orderService.getOrderOfMe());
    }
}
