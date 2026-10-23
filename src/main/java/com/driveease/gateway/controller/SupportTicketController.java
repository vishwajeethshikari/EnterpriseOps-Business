
package com.driveease.gateway.controller;

import com.driveease.gateway.entity.SupportTicket;
import com.driveease.gateway.service.SupportTicketService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tickets")
public class SupportTicketController {

    private final SupportTicketService ticketService;

    public SupportTicketController(
            SupportTicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/{ticketNumber}")
    public SupportTicket getTicket(
            @PathVariable String ticketNumber) {
        return ticketService.getTicket(ticketNumber);
    }
}
