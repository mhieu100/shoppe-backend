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
    private final UserDiscountRepository userDiscountRepository;

    private BigDecimal applyDiscount(BigDecimal amount, UserDiscount userDiscount) {
        if (userDiscount != null && userDiscount.getDiscount() != null) {
            BigDecimal discountValue = BigDecimal.valueOf(userDiscount.getDiscount().getValue());
            return amount.subtract(discountValue);
        }
        return amount;
    }

    @Transactional
    public OrderDTO createDirectOrder(int product_id, OrderRequestDTO orderRequest)
            throws ExistException, NotAllowException {
        String email = JwtUtils.getCurrentUserLogin().isPresent() ? JwtUtils.getCurrentUserLogin().get() : "";
        User user = userRepository.findByEmail(email).get();
        System.out.println(user);

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(orderRequest.getShippingAddress());
        order.setOrder_date(new Date());
        order.setCreated_at(new Date());

        BigDecimal totalAmount = BigDecimal.ZERO;

        List<OrderItem> orderItems = new ArrayList<>();

        Product product = productRepository.findById(product_id)
                .orElseThrow(() -> new ExistException("Product not found"));

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setOrder(order);
        orderItem.setQuantity(1);
        orderItem.setPrice(product.getPrice());
        orderItem.setStatus(OrderStatus.PROCESSING);

        orderItems.add(orderItem);

        order.setOrderItems(orderItems);
        totalAmount = order.calculateApprovedTotal();
        if (orderRequest.getDiscountCode() != null) {
            UserDiscount userDiscount = userDiscountRepository.findByUserAndDiscountCode(user, orderRequest.getDiscountCode())
                    .orElseThrow(() -> new ExistException("Invalid discount code"));

            if (userDiscount.isUsed()) {
                throw new ExistException("Discount code already used");
            }

            totalAmount = applyDiscount(totalAmount, userDiscount);
            userDiscount.setUsed(true);
            userDiscountRepository.save(userDiscount);
        }
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
                .toList();

        if (cartItemDTOS.isEmpty()) {
            throw new ExistException("Cart is empty, cannot place order.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(orderRequest.getShippingAddress());
        order.setOrder_date(new Date());
        order.setCreated_at(new Date());

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
            orderItem.setStatus(OrderStatus.PROCESSING);

            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(order.calculateApprovedTotal());
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
        orderDTO.setTotalAmount(order.getTotalAmount());
        orderDTO.setOrderItem(order.getOrderItems().stream().map(OrderItemDTO::new).collect(Collectors.toList()));
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

    public OrderItemDTO updateItemStatus(int itemId, OrderStatus status) throws NotFoundException {
        Optional<OrderItem> item = orderItemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException("Order not found");
        }

        if (item.get().getProduct().getStockQuantity() < item.get().getQuantity() && status == OrderStatus.PROCESSING) {
            throw new RuntimeException("Insufficient stock");
        }

        item.get().setStatus(status);
        OrderItem orderSaved = orderItemRepository.save(item.get());

        return new OrderItemDTO(orderSaved);
    }

    public OrderDTO getOrder(int orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setTotalAmount(order.calculateApprovedTotal());

        return convertToDTO(order);
    }

    public List<OrderItemDTO> getOrderOfMe() {
        String email = JwtUtils.getCurrentUserLogin().isPresent() ? JwtUtils.getCurrentUserLogin().get() : "";
        User user = userRepository.findByEmail(email).get();

        List<OrderItem> order = orderItemRepository.findByProductUser(user.getEmail());

        List<OrderItemDTO> listItemDTO = order.stream()
                .map(OrderItemDTO::new)
                .collect(Collectors.toList());
        return listItemDTO;
    }
}
