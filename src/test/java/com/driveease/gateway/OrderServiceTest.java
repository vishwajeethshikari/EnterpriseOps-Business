
package com.driveease.gateway;

import com.driveease.gateway.dto.CreateOrderRequest;
import com.driveease.gateway.entity.Order;
import com.driveease.gateway.exception.ResourceNotFoundException;
import com.driveease.gateway.repository.OrderRepository;
import com.driveease.gateway.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setOrderNumber("ORD-1045");
        order.setStatus("PROCESSING");
    }

    @Test
    void getOrderByOrderNumberShouldReturnOrder() {
        when(orderRepository.findByOrderNumber("ORD-1045"))
                .thenReturn(Optional.of(order));

        Order result = orderService.getOrderByOrderNumber("ORD-1045");

        assertNotNull(result);
        assertEquals("ORD-1045", result.getOrderNumber());
        assertEquals("PROCESSING", result.getStatus());

        verify(orderRepository).findByOrderNumber("ORD-1045");
    }

    @Test
    void getOrderByOrderNumberShouldThrowWhenNotFound() {
        when(orderRepository.findByOrderNumber("ORD-9999"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.getOrderByOrderNumber("ORD-9999")
        );
    }

    @Test
    void createOrderShouldSaveValidOrder() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setOrderNumber("ORD-1045");
        request.setStatus("PROCESSING");

        when(orderRepository.findByOrderNumber("ORD-1045"))
                .thenReturn(Optional.empty());

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.createOrder(request);

        assertNotNull(result);
        assertEquals("ORD-1045", result.getOrderNumber());
        assertEquals("PROCESSING", result.getStatus());

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrderShouldRejectInvalidOrderNumber() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setOrderNumber("INVALID");
        request.setStatus("PROCESSING");

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(request)
        );

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrderShouldRejectMissingStatus() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setOrderNumber("ORD-1046");
        request.setStatus(" ");

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(request)
        );

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrderShouldRejectDuplicateOrderNumber() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setOrderNumber("ORD-1045");
        request.setStatus("PROCESSING");

        when(orderRepository.findByOrderNumber("ORD-1045"))
                .thenReturn(Optional.of(order));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(request)
        );

        verify(orderRepository, never()).save(any(Order.class));
    }
}
