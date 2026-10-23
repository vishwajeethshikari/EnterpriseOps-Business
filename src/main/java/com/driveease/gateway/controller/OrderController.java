
package com.driveease.gateway.controller;

import com.driveease.gateway.entity.Order;
import com.driveease.gateway.service.OrderService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.driveease.gateway.dto.CreateOrderRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{orderNumber}")
    public Order getOrder(@PathVariable String orderNumber) {

        if (orderNumber == null
                || !orderNumber.matches("ORD-[0-9]{4,}")) {
            throw new IllegalArgumentException(
                    "Order number must look like ORD-1042");
        }

        return orderService.getOrderByOrderNumber(orderNumber);
    }

@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public Order createOrder(
        @RequestBody CreateOrderRequest request) {
    return orderService.createOrder(request);
}

}
