
package com.driveease.gateway.service;

import com.driveease.gateway.dto.CreateTicketRequest;
import com.driveease.gateway.entity.SupportTicket;
import com.driveease.gateway.exception.ResourceNotFoundException;
import com.driveease.gateway.repository.OrderRepository;
import com.driveease.gateway.repository.SupportTicketRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SupportTicketService {

    private final SupportTicketRepository ticketRepository;
    private final OrderRepository orderRepository;

    public SupportTicketService(
            SupportTicketRepository ticketRepository,
            OrderRepository orderRepository) {
        this.ticketRepository = ticketRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public SupportTicket createTicket(CreateTicketRequest request) {

        if (request.getOrderNumber() == null
                || request.getOrderNumber().isBlank()) {
            throw new IllegalArgumentException(
                    "Order number is required");
        }

        if (request.getIssueDescription() == null
                || request.getIssueDescription().isBlank()) {
            throw new IllegalArgumentException(
                    "Issue description is required");
        }

        String orderNumber = request.getOrderNumber().trim();

        if (orderRepository.findByOrderNumber(orderNumber).isEmpty()) {
            throw new ResourceNotFoundException(
                    "Order not found: " + orderNumber);
        }

        String priority = request.getPriority() == null
                || request.getPriority().isBlank()
                ? "MEDIUM"
                : request.getPriority().trim().toUpperCase();

        if (!priority.matches("LOW|MEDIUM|HIGH|URGENT")) {
            throw new IllegalArgumentException(
                    "Priority must be LOW, MEDIUM, HIGH, or URGENT");
        }

        SupportTicket ticket = new SupportTicket();
        ticket.setTicketNumber(
                "TKT-" + UUID.randomUUID().toString()
                        .substring(0, 8).toUpperCase());
        ticket.setOrderNumber(orderNumber);
        ticket.setIssueDescription(
                request.getIssueDescription().trim());
        ticket.setPriority(priority);
        ticket.setStatus("OPEN");

        return ticketRepository.save(ticket);
    }

    public SupportTicket getTicket(String ticketNumber) {
        return ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ticket not found: " + ticketNumber));
    }
}
