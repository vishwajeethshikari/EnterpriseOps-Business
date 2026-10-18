
package com.driveease.gateway.dto;

import java.time.LocalDate;

public class CreateOrderRequest {

    private String orderNumber;
    private String status;
    private LocalDate expectedDeliveryAt;

    public CreateOrderRequest() {
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getExpectedDeliveryAt() {
        return expectedDeliveryAt;
    }

    public void setExpectedDeliveryAt(
            LocalDate expectedDeliveryAt) {
        this.expectedDeliveryAt = expectedDeliveryAt;
    }
}
