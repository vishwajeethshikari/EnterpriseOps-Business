
package com.driveease.gateway.controller;

import com.driveease.gateway.dto.CreateTicketRequest;
import com.driveease.gateway.entity.SupportTicket;
import com.driveease.gateway.service.SupportTicketService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tickets")
public class SupportTicketController {

    private final SupportTicketService ticketService;

    public SupportTicketController(
            SupportTicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SupportTicket createTicket(
            @RequestBody CreateTicketRequest request) {
        return ticketService.createTicket(request);
    }

    @GetMapping("/{ticketNumber}")
    public SupportTicket getTicket(
            @PathVariable String ticketNumber) {
        return ticketService.getTicket(ticketNumber);
    }
}
