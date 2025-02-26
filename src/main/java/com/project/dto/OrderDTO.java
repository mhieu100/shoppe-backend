package com.project.dto;

import com.project.enums.OrderStatus;
import com.project.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private int id;
    private String customerName;
    private String customerEmail;
    private Date orderDate;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String shippingAddress;

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.customerName = order.getUser() != null ? order.getUser().getFullname() : null;
        this.customerEmail = order.getUser() != null ? order.getUser().getUsername() : null;
        this.orderDate = order.getOrder_date();
        this.status = order.getStatus();
        this.totalAmount = order.getTotalAmount();
        this.shippingAddress = order.getShippingAddress();
    }
}