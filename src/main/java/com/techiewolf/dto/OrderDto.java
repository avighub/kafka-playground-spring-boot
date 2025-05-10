package com.techiewolf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(setterPrefix = "set")
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private String orderId;          // UUID
    private String transactionId;    // UUID
    private String customerEmail;
    private String shippingAddress;
    private BigDecimal totalAmount;
    private String paymentMethod;    // "CREDIT_CARD", "PAYPAL", etc.

    public static OrderDto getInstance() {
        return OrderDto.builder()
                .setOrderId(java.util.UUID.randomUUID().toString())
                .setTransactionId(java.util.UUID.randomUUID().toString())
                .setCustomerEmail("")
                .setShippingAddress("123 Main St, Springfield, USA")
                .setTotalAmount(new BigDecimal("99.99"))
                .setPaymentMethod("CREDIT_CARD")
                .build();
    }
}
