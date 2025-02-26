package com.project.dto;

import lombok.Data;

@Data
public class MomoPaymentResponse {
    private String requestId;
    private String errorCode;
    private String orderId;
    private String message;
    private String localMessage;
    private String payUrl;
    private String signature;
    // Các trường khác nếu cần
}
