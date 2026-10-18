
package com.driveease.gateway.service;

import com.driveease.gateway.dto.ApproveWorkflowRequest;
import com.driveease.gateway.dto.CreateTicketRequest;
import com.driveease.gateway.dto.CreateWorkflowRequest;
import com.driveease.gateway.dto.RejectWorkflowRequest;
import com.driveease.gateway.entity.SupportTicket;
import com.driveease.gateway.entity.Workflow;
import com.driveease.gateway.repository.OrderRepository;
import com.driveease.gateway.repository.WorkflowRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final OrderRepository orderRepository;
    private final SupportTicketService ticketService;

    public WorkflowService(
            WorkflowRepository workflowRepository,
            OrderRepository orderRepository,
            SupportTicketService ticketService) {

        this.workflowRepository = workflowRepository;
        this.orderRepository = orderRepository;
        this.ticketService = ticketService;
    }

    // Create a workflow that requires approval.
    @Transactional
    public Workflow createWorkflow(CreateWorkflowRequest request) {

        String orderNumber = request.orderNumber().trim();

        // Verify that the order exists.
        if (orderRepository.findByOrderNumber(orderNumber).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Order not found: " + orderNumber
            );
        }

        Workflow workflow = new Workflow();

        workflow.setWorkflowNumber(
                "WF-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase()
        );

        workflow.setOrderNumber(orderNumber);
        workflow.setWorkflowType("SUPPORT_TICKET");
        workflow.setProposedIssueDescription(
                request.issueDescription().trim()
        );
        workflow.setProposedPriority(
                request.priority().trim().toUpperCase()
        );
        workflow.setStatus("PENDING_APPROVAL");

        return workflowRepository.save(workflow);
    }

    // Retrieve a workflow using its workflow number.
    @Transactional(readOnly = true)
    public Workflow getWorkflow(String workflowNumber) {

        return workflowRepository.findByWorkflowNumber(workflowNumber)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Workflow not found: " + workflowNumber
                ));
    }

    // Approve a workflow and create its support ticket.
    @Transactional
    public Workflow approveWorkflow(
            String workflowNumber,
            ApproveWorkflowRequest request) {

        Workflow workflow = getWorkflow(workflowNumber);

        if (!"PENDING_APPROVAL".equals(workflow.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only pending workflows can be approved"
            );
        }

        CreateTicketRequest ticketRequest = new CreateTicketRequest();

        ticketRequest.setOrderNumber(workflow.getOrderNumber());
        ticketRequest.setIssueDescription(
                workflow.getProposedIssueDescription()
        );
        ticketRequest.setPriority(workflow.getProposedPriority());

        // Create the support ticket through the existing service.
        SupportTicket ticket = ticketService.createTicket(ticketRequest);

        workflow.setApprovalDecision("APPROVED");
        workflow.setApprovedBy(request.approvedBy().trim());
        workflow.setApprovalReason(request.reason().trim());
        workflow.setApprovedAt(LocalDateTime.now());
        workflow.setTicketNumber(ticket.getTicketNumber());
        workflow.setStatus("COMPLETED");

        return workflowRepository.save(workflow);
    }

    // Reject a workflow without creating a support ticket.
    @Transactional
    public Workflow rejectWorkflow(
            String workflowNumber,
            RejectWorkflowRequest request) {

        Workflow workflow = getWorkflow(workflowNumber);

        if (!"PENDING_APPROVAL".equals(workflow.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only pending workflows can be rejected"
            );
        }

        workflow.setApprovalDecision("REJECTED");
        workflow.setApprovedBy(request.rejectedBy().trim());
        workflow.setApprovalReason(request.reason().trim());
        workflow.setApprovedAt(LocalDateTime.now());
        workflow.setStatus("REJECTED");

        return workflowRepository.save(workflow);
    }
}
