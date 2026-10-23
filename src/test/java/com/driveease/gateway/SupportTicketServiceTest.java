
package com.driveease.gateway;

import com.driveease.gateway.dto.CreateTicketRequest;
import com.driveease.gateway.entity.Order;
import com.driveease.gateway.entity.SupportTicket;
import com.driveease.gateway.exception.ResourceNotFoundException;
import com.driveease.gateway.repository.OrderRepository;
import com.driveease.gateway.repository.SupportTicketRepository;
import com.driveease.gateway.service.SupportTicketService;

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
class SupportTicketServiceTest {

    @Mock
    private SupportTicketRepository ticketRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private SupportTicketService ticketService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setOrderNumber("ORD-1045");
        order.setStatus("PROCESSING");
    }

    @Test
    void createTicketShouldSaveValidTicket() {
        CreateTicketRequest request = new CreateTicketRequest();
        request.setOrderNumber("ORD-1045");
        request.setIssueDescription("Order delivery is delayed");
        request.setPriority("HIGH");

        when(orderRepository.findByOrderNumber("ORD-1045"))
                .thenReturn(Optional.of(order));

        when(ticketRepository.save(any(SupportTicket.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SupportTicket result = ticketService.createTicket(request);

        assertNotNull(result);
        assertTrue(result.getTicketNumber().startsWith("TKT-"));
        assertEquals("ORD-1045", result.getOrderNumber());
        assertEquals("Order delivery is delayed",
                result.getIssueDescription());
        assertEquals("HIGH", result.getPriority());
        assertEquals("OPEN", result.getStatus());

        verify(ticketRepository).save(any(SupportTicket.class));
    }

    @Test
    void createTicketShouldUseMediumPriorityWhenMissing() {
        CreateTicketRequest request = new CreateTicketRequest();
        request.setOrderNumber("ORD-1045");
        request.setIssueDescription("Please check my order");
        request.setPriority(null);

        when(orderRepository.findByOrderNumber("ORD-1045"))
                .thenReturn(Optional.of(order));

        when(ticketRepository.save(any(SupportTicket.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SupportTicket result = ticketService.createTicket(request);

        assertEquals("MEDIUM", result.getPriority());
        assertEquals("OPEN", result.getStatus());
    }

    @Test
    void createTicketShouldNormalizePriority() {
        CreateTicketRequest request = new CreateTicketRequest();
        request.setOrderNumber("ORD-1045");
        request.setIssueDescription("Please check my order");
        request.setPriority(" high ");

        when(orderRepository.findByOrderNumber("ORD-1045"))
                .thenReturn(Optional.of(order));

        when(ticketRepository.save(any(SupportTicket.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SupportTicket result = ticketService.createTicket(request);

        assertEquals("HIGH", result.getPriority());
    }

    @Test
    void createTicketShouldRejectMissingOrderNumber() {
        CreateTicketRequest request = new CreateTicketRequest();
        request.setOrderNumber(" ");
        request.setIssueDescription("Order delivery is delayed");
        request.setPriority("HIGH");

        assertThrows(
                IllegalArgumentException.class,
                () -> ticketService.createTicket(request)
        );

        verifyNoInteractions(orderRepository, ticketRepository);
    }

    @Test
    void createTicketShouldRejectMissingDescription() {
        CreateTicketRequest request = new CreateTicketRequest();
        request.setOrderNumber("ORD-1045");
        request.setIssueDescription(" ");
        request.setPriority("HIGH");

        assertThrows(
                IllegalArgumentException.class,
                () -> ticketService.createTicket(request)
        );

        verifyNoInteractions(orderRepository, ticketRepository);
    }

    @Test
    void createTicketShouldRejectUnknownOrder() {
        CreateTicketRequest request = new CreateTicketRequest();
        request.setOrderNumber("ORD-9999");
        request.setIssueDescription("Order delivery is delayed");
        request.setPriority("HIGH");

        when(orderRepository.findByOrderNumber("ORD-9999"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> ticketService.createTicket(request)
        );

        verify(ticketRepository, never()).save(any(SupportTicket.class));
    }

    @Test
    void createTicketShouldRejectInvalidPriority() {
        CreateTicketRequest request = new CreateTicketRequest();
        request.setOrderNumber("ORD-1045");
        request.setIssueDescription("Order delivery is delayed");
        request.setPriority("INVALID");

        when(orderRepository.findByOrderNumber("ORD-1045"))
                .thenReturn(Optional.of(order));

        assertThrows(
                IllegalArgumentException.class,
                () -> ticketService.createTicket(request)
        );

        verify(ticketRepository, never()).save(any(SupportTicket.class));
    }

    @Test
    void getTicketShouldReturnExistingTicket() {
        SupportTicket ticket = new SupportTicket();
        ticket.setTicketNumber("TKT-AB12CD34");
        ticket.setOrderNumber("ORD-1045");
        ticket.setIssueDescription("Order delivery is delayed");
        ticket.setPriority("HIGH");
        ticket.setStatus("OPEN");

        when(ticketRepository.findByTicketNumber("TKT-AB12CD34"))
                .thenReturn(Optional.of(ticket));

        SupportTicket result = ticketService.getTicket("TKT-AB12CD34");

        assertNotNull(result);
        assertEquals("TKT-AB12CD34", result.getTicketNumber());
        assertEquals("ORD-1045", result.getOrderNumber());
    }

    @Test
    void getTicketShouldThrowWhenNotFound() {
        when(ticketRepository.findByTicketNumber("TKT-NOTFOUND"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> ticketService.getTicket("TKT-NOTFOUND")
        );
    }
}
