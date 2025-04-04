package com.project.dto;

import com.project.enums.OrderStatus;
import com.project.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private int id;
    private String customerName;
    private String email;
    private String productName;
    private String shippingAddress;
    private OrderStatus orderStatus;
    private Date orderDate;
    private int quantity;
    private BigDecimal price;

    public OrderItemDTO(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.customerName = orderItem.getOrder().getUser().getFullname();
        this.email = orderItem.getOrder().getUser().getEmail();
        this.productName = orderItem.getProduct().getName();
        this.orderStatus = orderItem.getStatus();
        this.shippingAddress = orderItem.getOrder().getShippingAddress();
        this.quantity = orderItem.getQuantity();
        this.price = orderItem.getPrice();
        this.orderDate = orderItem.getOrder().getOrder_date();
    }
}
