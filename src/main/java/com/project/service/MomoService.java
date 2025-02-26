package com.project.service;


import com.project.dto.MomoPaymentRequest;
import com.project.dto.MomoPaymentResponse;
import com.project.dto.OrderDTO;
import com.project.enums.OrderStatus;
import com.project.enums.PaymentStatus;
import com.project.exception.NotAllowException;
import com.project.exception.NotFoundException;
import com.project.model.Order;
import com.project.model.Payment;
import com.project.repository.OrderRepository;
import com.project.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.project.util.HmacUtil;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class MomoService {
    private final static Logger logger = LoggerFactory.getLogger(MomoService.class);
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    @Value("${momo.partnerCode}")
    private String partnerCode;
    @Value("${momo.accessKey}")
    private String accessKey;
    @Value("${momo.secretKey}")
    private String secretKey;
    @Value("${momo.endpoint}")
    private String endpoint;

    public MomoService(OrderRepository orderRepository, PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    public MomoPaymentResponse createMomoPaymentRequest(OrderDTO order) {
        MomoPaymentRequest request = new MomoPaymentRequest();

        // Thiết lập các thông tin từ entity Order
        request.setOrderId(String.valueOf(order.getId()));
//        BigDecimal totalAmount = order.getTotalAmount();
//        long amountInLong = totalAmount.setScale(0, RoundingMode.HALF_UP).longValue();
//        request.setAmount(String.valueOf(amountInLong));
        request.setAmount(String.valueOf(8000000));
        request.setOrderInfo("Order ID: " + order.getId() + ", Date: " + order.getOrderDate() + ", Address: " + order.getShippingAddress());
        request.setRedirectUrl("https://bookas.vn");
        request.setIpnUrl("https://bookas.vn");
        request.setExtraData("");
        request.setLang("vi");
        request.setRequestId(UUID.randomUUID().toString());

        // Các thông tin khác từ cấu hình
        request.setPartnerCode(partnerCode);
        request.setAccessKey(accessKey);
        request.setRequestType("payWithMethod");

        // Tạo signature
        // Set tất cả dữ liệu trước
        request.setPartnerCode(partnerCode);
        request.setAccessKey(accessKey);

        String rawHash = "accessKey=" + accessKey +
                "&amount=" + request.getAmount() +
                "&extraData=" + request.getExtraData() +
                "&ipnUrl=" + request.getIpnUrl() +
                "&orderId=" + request.getOrderId() +
                "&orderInfo=" + request.getOrderInfo() +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + request.getRedirectUrl() +
                "&requestId=" + request.getRequestId() +
                "&requestType=" + request.getRequestType();

        String signature = HmacUtil.calculateHMac(rawHash, secretKey);

        request.setSignature(signature);

        // Gọi API Momo
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MomoPaymentRequest> entity = new HttpEntity<>(request, headers);
        logger.info("Momo payment request: " + request);
        ResponseEntity<MomoPaymentResponse> response = restTemplate.postForEntity(endpoint, entity, MomoPaymentResponse.class);
        logger.info("Received response from Momo: {}", response.getBody());

        return response.getBody();
    }

    public void callbackMomoPaymentResponse(Map<String, String> callbackData) throws NotAllowException, NotFoundException {
        String orderId = callbackData.get("orderId");
        String resultCode = callbackData.get("resultCode");
//        String message = callbackData.get("message");
//        String signature = callbackData.get("signature");

        // Xác thực chữ ký (nếu cần)
        boolean isValidSignature = validateSignature(callbackData);
        if (!isValidSignature) {
            throw new NotAllowException("Invalid signature");
        }

        // Tìm đơn hàng trong database
        Optional<Order> orderOptional = orderRepository.findById(Integer.parseInt(orderId));
        if (orderOptional.isEmpty()) {
            throw new NotFoundException("Order not found");
        }

        Order order = orderOptional.get();

        Payment payment = paymentRepository.findByOrderId(order.getId());

        // Xử lý kết quả thanh toán
        if ("0".equals(resultCode)) {
            // Thanh toán thành công
            order.setStatus(OrderStatus.PROCESSING);
            order.setUpdated_at(new Date());

            orderRepository.save(order);

            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaymentDate(new Date());
            paymentRepository.save(payment);

//            sendConfirmationEmail(order);
        } else {
            // Thanh toán thất bại
            order.setStatus(OrderStatus.CANCELLED);
            order.setUpdated_at(new Date());
            orderRepository.save(order);

            payment.setStatus(PaymentStatus.FAILED);
            payment.setPaymentDate(new Date());
            paymentRepository.save(payment);
        }
    }

    private boolean validateSignature(Map<String, String> callbackData) {
        // Tạo raw hash từ dữ liệu callback
        String rawHash = "partnerCode=" + callbackData.get("partnerCode") +
                "&orderId=" + callbackData.get("orderId") +
                "&requestId=" + callbackData.get("requestId") +
                "&amount=" + callbackData.get("amount") +
                "&orderInfo=" + callbackData.get("orderInfo") +
                "&orderType=" + callbackData.get("orderType") +
                "&transId=" + callbackData.get("transId") +
                "&resultCode=" + callbackData.get("resultCode") +
                "&message=" + callbackData.get("message") +
                "&payType=" + callbackData.get("payType") +
                "&responseTime=" + callbackData.get("responseTime") +
                "&extraData=" + callbackData.get("extraData");

        // Tính toán signature
        String calculatedSignature = HmacUtil.calculateHMac(rawHash, secretKey);

        // So sánh signature
        return calculatedSignature.equals(callbackData.get("signature"));
    }
}
