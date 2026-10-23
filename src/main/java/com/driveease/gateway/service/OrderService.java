
package com.driveease.gateway.service;

import com.driveease.gateway.entity.Order;
import com.driveease.gateway.exception.ResourceNotFoundException;
import com.driveease.gateway.repository.OrderRepository;
import org.springframework.stereotype.Service;

import com.driveease.gateway.dto.CreateOrderRequest;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found: " + orderNumber));
    }

@Transactional
public Order createOrder(CreateOrderRequest request) {

    if (request.getOrderNumber() == null
            || !request.getOrderNumber()
                    .matches("ORD-[0-9]{4,}")) {
        throw new IllegalArgumentException(
                "Order number must look like ORD-1045");
    }

    if (request.getStatus() == null
            || request.getStatus().isBlank()) {
        throw new IllegalArgumentException(
                "Order status is required");
    }

    if (orderRepository.findByOrderNumber(
            request.getOrderNumber()).isPresent()) {
        throw new IllegalArgumentException(
                "Order number already exists");
    }

    Order order = new Order();
    order.setOrderNumber(request.getOrderNumber());
    order.setStatus(request.getStatus());
    order.setExpectedDeliveryAt(
            request.getExpectedDeliveryAt());

    return orderRepository.save(order);
}

}
