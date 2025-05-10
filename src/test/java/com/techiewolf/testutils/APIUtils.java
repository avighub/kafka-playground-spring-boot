package com.techiewolf.testutils;

import com.techiewolf.dto.OrderDto;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.restassured.RestAssured.given;

@Component
public final class APIUtils {
    private final String baseUrl;
    private final String createOrderEndpoint;
    private final String notificationEndpoint;
    private final String paymentEndpoint;

    public APIUtils(
            @Value("${app.api.base.url}") String baseUrl,
            @Value("${app.api.create.order.endpoint}") String createOrderEndpoint,
            @Value("${app.api.notification.endpoint}") String notificationEndpoint,
            @Value("${app.api.payment.endpoint}") String paymentEndpoint) {
        this.baseUrl = baseUrl;
        this.createOrderEndpoint = createOrderEndpoint;
        this.notificationEndpoint = notificationEndpoint;
        this.paymentEndpoint = paymentEndpoint;
    }

    private RequestSpecification getBasicSpec() {
        return new RequestSpecBuilder()
                .addHeader("Content-Type", "application/json")
                .setBaseUri(this.baseUrl)
                .build();
    }

    public Response sendPostRequest(Object request, String endpoint) {
        return given()
                .spec(getBasicSpec())
                .body(request)
                .log().all()
                .post(endpoint)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response createOrders(List<OrderDto> orders) {
        return sendPostRequest(orders, createOrderEndpoint);
    }

    public Response sendMessageToNotificationsTopic(List<OrderDto> orders) {
        return sendPostRequest(orders, notificationEndpoint);
    }

    public Response sendMessageToPaymentsTopic(List<OrderDto> orders) {
        return sendPostRequest(orders, paymentEndpoint);
    }


}
