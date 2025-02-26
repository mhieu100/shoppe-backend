package com.project.controller;

import com.project.dto.MomoPaymentResponse;
import com.project.dto.OrderDTO;
import com.project.model.Order;
import com.project.service.MomoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final MomoService service;

    @PostMapping("/momo")
    public ResponseEntity<MomoPaymentResponse> momoPayment(@RequestBody OrderDTO order) throws UnsupportedEncodingException {
        MomoPaymentResponse response = service.createMomoPaymentRequest(order);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/callback")
    public ResponseEntity<?> callback() {
//        service.callbackMomoPaymentResponse();
        return ResponseEntity.ok().body("Received");
    }
}
