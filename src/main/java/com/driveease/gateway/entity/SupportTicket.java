
package com.driveease.gateway.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "support_tickets")
public class SupportTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_number", nullable = false, unique = true)
    private String ticketNumber;

    @Column(name = "order_number", nullable = false)
    private String orderNumber;

    @Column(name = "issue_description", nullable = false)
    private String issueDescription;

    @Column(nullable = false)
    private String priority = "MEDIUM";

    @Column(nullable = false)
    private String status = "OPEN";

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public SupportTicket() {}

    public Long getId() { return id; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getIssueDescription() { return issueDescription; }
    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public String getPriority() { return priority; }
    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
