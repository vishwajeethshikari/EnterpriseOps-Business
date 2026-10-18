
package com.driveease.gateway.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workflows")
public class Workflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workflow_number", nullable = false, unique = true, length = 50)
    private String workflowNumber;

    @Column(name = "order_number", nullable = false, length = 50)
    private String orderNumber;

    @Column(name = "workflow_type", nullable = false, length = 50)
    private String workflowType = "SUPPORT_TICKET";

    @Column(nullable = false, length = 30)
    private String status = "PENDING_APPROVAL";

    @Column(name = "proposed_issue_description", nullable = false, columnDefinition = "TEXT")
    private String proposedIssueDescription;

    @Column(name = "proposed_priority", nullable = false, length = 20)
    private String proposedPriority = "MEDIUM";

    @Column(name = "approval_decision", length = 20)
    private String approvalDecision;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(name = "approval_reason", columnDefinition = "TEXT")
    private String approvalReason;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "ticket_number", length = 50)
    private String ticketNumber;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Workflow() {
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getWorkflowNumber() { return workflowNumber; }
    public void setWorkflowNumber(String workflowNumber) {
        this.workflowNumber = workflowNumber;
    }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getWorkflowType() { return workflowType; }
    public void setWorkflowType(String workflowType) {
        this.workflowType = workflowType;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getProposedIssueDescription() {
        return proposedIssueDescription;
    }
    public void setProposedIssueDescription(String proposedIssueDescription) {
        this.proposedIssueDescription = proposedIssueDescription;
    }

    public String getProposedPriority() { return proposedPriority; }
    public void setProposedPriority(String proposedPriority) {
        this.proposedPriority = proposedPriority;
    }

    public String getApprovalDecision() { return approvalDecision; }
    public void setApprovalDecision(String approvalDecision) {
        this.approvalDecision = approvalDecision;
    }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getApprovalReason() { return approvalReason; }
    public void setApprovalReason(String approvalReason) {
        this.approvalReason = approvalReason;
    }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
