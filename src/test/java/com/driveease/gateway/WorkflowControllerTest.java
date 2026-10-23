package com.driveease.gateway;

import com.driveease.gateway.controller.WorkflowController;
import com.driveease.gateway.dto.ApproveWorkflowRequest;
import com.driveease.gateway.dto.CreateWorkflowRequest;
import com.driveease.gateway.dto.RejectWorkflowRequest;
import com.driveease.gateway.entity.Workflow;
import com.driveease.gateway.service.WorkflowService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(WorkflowController.class)
class WorkflowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WorkflowService workflowService;

    private Workflow workflow;

    @BeforeEach
    void setUp() {
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
    void createWorkflowShouldReturnCreated() throws Exception {
        CreateWorkflowRequest request = new CreateWorkflowRequest(
                "ORD-1045",
                "Order delivery is delayed",
                "HIGH"
        );

        when(workflowService.createWorkflow(any(CreateWorkflowRequest.class)))
                .thenReturn(workflow);

        mockMvc.perform(post("/api/v1/workflows")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.workflowNumber")
                        .value("WF-A7A46B99"))
                .andExpect(jsonPath("$.status")
                        .value("PENDING_APPROVAL"));

        verify(workflowService).createWorkflow(
                any(CreateWorkflowRequest.class)
        );
    }

    @Test
    void getWorkflowShouldReturnWorkflow() throws Exception {
        when(workflowService.getWorkflow("WF-A7A46B99"))
                .thenReturn(workflow);

        mockMvc.perform(get("/api/v1/workflows/WF-A7A46B99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workflowNumber")
                        .value("WF-A7A46B99"))
                .andExpect(jsonPath("$.orderNumber")
                        .value("ORD-1045"))
                .andExpect(jsonPath("$.status")
                        .value("PENDING_APPROVAL"));

        verify(workflowService).getWorkflow("WF-A7A46B99");
    }

    @Test
    void approveWorkflowShouldReturnCompletedWorkflow()
            throws Exception {
        ApproveWorkflowRequest request =
                mock(ApproveWorkflowRequest.class);

        Workflow completed = new Workflow();
        completed.setWorkflowNumber("WF-A7A46B99");
        completed.setOrderNumber("ORD-1045");
        completed.setWorkflowType("SUPPORT_TICKET");
        completed.setProposedIssueDescription(
                "Order delivery is delayed"
        );
        completed.setProposedPriority("HIGH");
        completed.setStatus("COMPLETED");
        completed.setApprovalDecision("APPROVED");
        completed.setTicketNumber("TKT-AB12CD34");

        when(workflowService.approveWorkflow(
                eq("WF-A7A46B99"),
                any(ApproveWorkflowRequest.class)))
                .thenReturn(completed);

        // Use a valid request body matching your DTO's actual fields.
        String requestJson = """
                {
                  "approvedBy": "manager",
                  "reason": "Verified order issue"
                }
                """;

        mockMvc.perform(post(
                "/api/v1/workflows/WF-A7A46B99/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.approvalDecision")
                        .value("APPROVED"))
                .andExpect(jsonPath("$.ticketNumber")
                        .value("TKT-AB12CD34"));

        verify(workflowService).approveWorkflow(
                eq("WF-A7A46B99"),
                any(ApproveWorkflowRequest.class)
        );
    }

    @Test
    void rejectWorkflowShouldReturnRejectedWorkflow()
            throws Exception {
        Workflow rejected = new Workflow();
        rejected.setWorkflowNumber("WF-A7A46B99");
        rejected.setOrderNumber("ORD-1045");
        rejected.setWorkflowType("SUPPORT_TICKET");
        rejected.setProposedIssueDescription(
                "Order delivery is delayed"
        );
        rejected.setProposedPriority("HIGH");
        rejected.setStatus("REJECTED");
        rejected.setApprovalDecision("REJECTED");

        when(workflowService.rejectWorkflow(
                eq("WF-A7A46B99"),
                any(RejectWorkflowRequest.class)))
                .thenReturn(rejected);

        String requestJson = """
                {
                  "rejectedBy": "manager",
                  "reason": "Insufficient evidence"
                }
                """;

        mockMvc.perform(post(
                "/api/v1/workflows/WF-A7A46B99/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.approvalDecision")
                        .value("REJECTED"));

        verify(workflowService).rejectWorkflow(
                eq("WF-A7A46B99"),
                any(RejectWorkflowRequest.class)
        );
    }
}
