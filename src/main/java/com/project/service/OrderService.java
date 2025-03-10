package com.project.service;

import com.project.dto.*;
import com.project.enums.OrderStatus;
import com.project.enums.PaymentMethod;
import com.project.enums.PaymentStatus;
import com.project.exception.ExistException;
import com.project.exception.NotAllowException;
import com.project.exception.NotFoundException;
import com.project.model.*;
import com.project.repository.*;
import com.project.util.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final PaymentRepository paymentRepository;

    public OrderDTO createDirectOrder(int product_id, OrderRequestDTO orderRequest)
            throws ExistException, NotAllowException {
        String email = JwtUtils.getCurrentUserLogin().isPresent() ? JwtUtils.getCurrentUserLogin().get() : "";
        User user = userRepository.findByEmail(email).get();

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(orderRequest.getShippingAddress());
        order.setOrder_date(new Date());
        order.setCreated_at(new Date());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        List<OrderItem> orderItems = new ArrayList<>();

        Product product = productRepository.findById(product_id)
                .orElseThrow(() -> new ExistException("Product not found"));

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setOrder(order);
        orderItem.setQuantity(1);
        orderItem.setPrice(product.getPrice());

        totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));

        orderItems.add(orderItem);
        order.setTotalAmount(totalAmount);
        Order orderSaved = orderRepository.save(order);

        orderItemRepository.saveAll(orderItems);

        Payment payment = new Payment();
        payment.setAmount(totalAmount);
        payment.setPaymentMethod(orderRequest.getPaymentMethod());
        payment.setOrder(orderSaved);
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        return convertToDTO(orderSaved);
    }

    @Transactional
    public OrderDTO createOrderFromCart(OrderRequestDTO orderRequest) throws ExistException {
        String email = JwtUtils.getCurrentUserLogin().isPresent() ? JwtUtils.getCurrentUserLogin().get() : "";
        User user = userRepository.findByEmail(email).get();

        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(user);

        if (shoppingCart == null) {
            throw new ExistException("Shopping cart not found");
        }

        List<CartItemDTO> cartItemDTOS = shoppingCart.getCartItems().stream().map(CartItemDTO::new)
                .collect(Collectors.toList());

        if (cartItemDTOS.isEmpty()) {
            throw new ExistException("Cart is empty, cannot place order.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(orderRequest.getShippingAddress());
        order.setOrder_date(new Date());
        order.setCreated_at(new Date());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : shoppingCart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setOrder(order);
            if (cartItem.getProduct().getStockQuantity() < cartItem.getQuantity()) {
                throw new ExistException("Not enough stock for product: " + cartItem.getProduct().getName());
            }
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());

            totalAmount = totalAmount
                    .add(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        Order orderSaved = orderRepository.save(order);

        orderItemRepository.saveAll(orderItems);

        createPayment(orderSaved, totalAmount, orderRequest.getPaymentMethod());

        cartItemRepository.deleteAllByCartId(shoppingCart.getId());
        return convertToDTO(orderSaved);
    }

    private void createPayment(Order order, BigDecimal amount, PaymentMethod paymentMethod) {
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setOrder(order);
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);
    }

    public OrderDTO convertToDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setShippingAddress(order.getShippingAddress());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setTotalAmount(order.getTotalAmount());
        orderDTO.setOrderDate(order.getOrder_date());
        orderDTO.setCustomerName(order.getUser().getFullname());
        orderDTO.setCustomerEmail(order.getUser().getEmail());

        return orderDTO;
    }

    public Pagination getAllOrders(Specification<Order> specification, Pageable pageable) {
        Page<Order> pageOrder = orderRepository.findAll(specification, pageable);
        Pagination pagination = new Pagination();
        Pagination.Meta meta = new Pagination.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageOrder.getTotalPages());
        meta.setTotal(pageOrder.getTotalElements());

        pagination.setMeta(meta);

        List<OrderDTO> listOrders = pageOrder.getContent().stream()
                .map(OrderDTO::new)
                .collect(Collectors.toList());

        pagination.setResult(listOrders);

        return pagination;
    }

    public OrderDTO updateOrderStatus(int id, String status) throws NotFoundException {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty()) {
            throw new NotFoundException("Order not found");
        }
        OrderStatus statusUpdate = OrderStatus.valueOf(status);

        order.get().setStatus(statusUpdate);
        Order orderSaved = orderRepository.save(order.get());

        return convertToDTO(orderSaved);
    }

    public List<OrderDTO> getOrderOfMe() {
        String email = JwtUtils.getCurrentUserLogin().isPresent() ? JwtUtils.getCurrentUserLogin().get() : "";
        User user = userRepository.findByEmail(email).get();

        List<Order> order = orderRepository.findByUser(user);

        List<OrderDTO> listOrderDTO = order.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return listOrderDTO;
    }
}
