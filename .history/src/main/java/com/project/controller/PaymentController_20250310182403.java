package com.project.controller;

import com.project.dto.MomoPaymentResponse;
import com.project.dto.OrderDTO;
// import com.project.service.MomoService;
import com.project.service.VNPayService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    // private final MomoService service;
    private final VNPayService vnPayService;

    // @PostMapping("/momo")
    // public ResponseEntity<MomoPaymentResponse> momoPayment(@RequestBody OrderDTO order) throws UnsupportedEncodingException {
    //     MomoPaymentResponse response = service.createMomoPaymentRequest(order);
    //     return ResponseEntity.ok(response);
    // }

    // @PostMapping("/callback")
    // public ResponseEntity<?> callback() {
    //    service.callbackMomoPaymentResponse();
    //     return ResponseEntity.ok().body("Received");
    // }

     @PostMapping("/vn-pay/create-payment")
    public ResponseEntity<String> createPayment(@RequestParam long amount, HttpServletRequest request) throws UnsupportedEncodingException {
        String ipAddress = request.getRemoteAddr();
        String paymentUrl = vnPayService.createPaymentUrl(amount, ipAddress);
        return ResponseEntity.ok(paymentUrl);
    }

    @GetMapping("/return")
    public ResponseEntity<String> paymentReturn(@RequestParam Map<String, String> params) {
        // Handle payment return (verify response, update order status, etc.)
        return ResponseEntity.ok("Payment processed successfully");
    }
}
