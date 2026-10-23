
package com.driveease.gateway;

import com.driveease.gateway.dto.ApproveWorkflowRequest;
import com.driveease.gateway.dto.CreateTicketRequest;
import com.driveease.gateway.dto.CreateWorkflowRequest;
import com.driveease.gateway.dto.RejectWorkflowRequest;
import com.driveease.gateway.entity.Order;
import com.driveease.gateway.entity.SupportTicket;
import com.driveease.gateway.entity.Workflow;
import com.driveease.gateway.repository.OrderRepository;
import com.driveease.gateway.repository.WorkflowRepository;
import com.driveease.gateway.service.SupportTicketService;
import com.driveease.gateway.service.WorkflowService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowServiceTest {

    @Mock
    private WorkflowRepository workflowRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SupportTicketService ticketService;

    @InjectMocks
    private WorkflowService workflowService;

    private Order order;
    private Workflow workflow;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setOrderNumber("ORD-1045");
        order.setStatus("PROCESSING");

        workflow = new Workflow();
        workflow.setWorkflowNumber("WF-A7A46B99");
        workflow.setOrderNumber("ORD-1045");
        workflow.setWorkflowType("SUPPORT_TICKET");
        workflow.setProposedIssueDescription(
                "Order delivery is delayed"
        );
        workflow.setProposedPriority("HIGH");
        workflow.setStatus("PENDING_APPROVAL");
    }

    @Test
    void createWorkflowShouldSavePendingWorkflow() {
        CreateWorkflowRequest request = new CreateWorkflowRequest(
                "ORD-1045",
                "Order delivery is delayed",
                "HIGH"
        );

        when(orderRepository.findByOrderNumber("ORD-1045"))
                .thenReturn(Optional.of(order));

        when(workflowRepository.save(any(Workflow.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Workflow result = workflowService.createWorkflow(request);

        assertNotNull(result);
        assertTrue(result.getWorkflowNumber().startsWith("WF-"));
        assertEquals("ORD-1045", result.getOrderNumber());
        assertEquals("SUPPORT_TICKET", result.getWorkflowType());
        assertEquals("HIGH", result.getProposedPriority());
        assertEquals("PENDING_APPROVAL", result.getStatus());

        verify(workflowRepository).save(any(Workflow.class));
        verifyNoInteractions(ticketService);
    }

    @Test
    void createWorkflowShouldRejectUnknownOrder() {
        CreateWorkflowRequest request = new CreateWorkflowRequest(
                "ORD-9999",
                "Order delivery is delayed",
                "HIGH"
        );

        when(orderRepository.findByOrderNumber("ORD-9999"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> workflowService.createWorkflow(request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(workflowRepository, never()).save(any(Workflow.class));
        verifyNoInteractions(ticketService);
    }

    @Test
    void getWorkflowShouldReturnExistingWorkflow() {
        when(workflowRepository.findByWorkflowNumber("WF-A7A46B99"))
                .thenReturn(Optional.of(workflow));

        Workflow result = workflowService.getWorkflow("WF-A7A46B99");

        assertNotNull(result);
        assertEquals("WF-A7A46B99", result.getWorkflowNumber());
        assertEquals("PENDING_APPROVAL", result.getStatus());
    }

    @Test
    void getWorkflowShouldReturnNotFoundForUnknownWorkflow() {
        when(workflowRepository.findByWorkflowNumber("WF-UNKNOWN"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> workflowService.getWorkflow("WF-UNKNOWN")
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void approveWorkflowShouldCreateTicketAndCompleteWorkflow() {
        ApproveWorkflowRequest request = mock(ApproveWorkflowRequest.class);

        when(request.approvedBy()).thenReturn("manager");
        when(request.reason()).thenReturn("Verified order issue");

        SupportTicket ticket = new SupportTicket();
        ticket.setTicketNumber("TKT-AB12CD34");
        ticket.setOrderNumber("ORD-1045");
        ticket.setIssueDescription("Order delivery is delayed");
        ticket.setPriority("HIGH");
        ticket.setStatus("OPEN");

        when(workflowRepository.findByWorkflowNumber("WF-A7A46B99"))
                .thenReturn(Optional.of(workflow));

        when(ticketService.createTicket(any(CreateTicketRequest.class)))
                .thenReturn(ticket);

        when(workflowRepository.save(any(Workflow.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Workflow result = workflowService.approveWorkflow(
                "WF-A7A46B99",
                request
        );

        assertEquals("COMPLETED", result.getStatus());
        assertEquals("APPROVED", result.getApprovalDecision());
        assertEquals("manager", result.getApprovedBy());
        assertEquals("Verified order issue", result.getApprovalReason());
        assertEquals("TKT-AB12CD34", result.getTicketNumber());
        assertNotNull(result.getApprovedAt());

        ArgumentCaptor<CreateTicketRequest> captor =
                ArgumentCaptor.forClass(CreateTicketRequest.class);

        verify(ticketService).createTicket(captor.capture());

        assertEquals("ORD-1045", captor.getValue().getOrderNumber());
        assertEquals(
                "Order delivery is delayed",
                captor.getValue().getIssueDescription()
        );
        assertEquals("HIGH", captor.getValue().getPriority());

        verify(workflowRepository).save(workflow);
    }

    @Test
    void rejectWorkflowShouldNotCreateTicket() {
        RejectWorkflowRequest request = mock(RejectWorkflowRequest.class);

        when(request.rejectedBy()).thenReturn("manager");
        when(request.reason()).thenReturn("Insufficient evidence");

        when(workflowRepository.findByWorkflowNumber("WF-A7A46B99"))
                .thenReturn(Optional.of(workflow));

        when(workflowRepository.save(any(Workflow.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Workflow result = workflowService.rejectWorkflow(
                "WF-A7A46B99",
                request
        );

        assertEquals("REJECTED", result.getStatus());
        assertEquals("REJECTED", result.getApprovalDecision());
        assertEquals("manager", result.getApprovedBy());
        assertEquals(
                "Insufficient evidence",
                result.getApprovalReason()
        );
        assertNotNull(result.getApprovedAt());
        assertNull(result.getTicketNumber());

        verify(ticketService, never())
                .createTicket(any(CreateTicketRequest.class));

        verify(workflowRepository).save(workflow);
    }

    @Test
    void approveWorkflowShouldRejectAlreadyProcessedWorkflow() {
        workflow.setStatus("COMPLETED");

        when(workflowRepository.findByWorkflowNumber("WF-A7A46B99"))
                .thenReturn(Optional.of(workflow));

        ApproveWorkflowRequest request = mock(ApproveWorkflowRequest.class);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> workflowService.approveWorkflow(
                        "WF-A7A46B99",
                        request
                )
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

        verify(ticketService, never())
                .createTicket(any(CreateTicketRequest.class));
    }

    @Test
    void rejectWorkflowShouldRejectAlreadyProcessedWorkflow() {
        workflow.setStatus("REJECTED");

        when(workflowRepository.findByWorkflowNumber("WF-A7A46B99"))
                .thenReturn(Optional.of(workflow));

        RejectWorkflowRequest request = mock(RejectWorkflowRequest.class);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> workflowService.rejectWorkflow(
                        "WF-A7A46B99",
                        request
                )
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

        verify(ticketService, never())
                .createTicket(any(CreateTicketRequest.class));
    }
}
