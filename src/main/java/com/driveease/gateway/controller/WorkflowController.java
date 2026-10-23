
package com.driveease.gateway.controller;

import com.driveease.gateway.dto.ApproveWorkflowRequest;
import com.driveease.gateway.dto.CreateWorkflowRequest;
import com.driveease.gateway.dto.RejectWorkflowRequest;
import com.driveease.gateway.entity.Workflow;
import com.driveease.gateway.service.WorkflowService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workflows")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Workflow createWorkflow(
            @Valid @RequestBody CreateWorkflowRequest request) {
        return workflowService.createWorkflow(request);
    }

    @GetMapping("/{workflowNumber}")
    public Workflow getWorkflow(
            @PathVariable String workflowNumber) {
        return workflowService.getWorkflow(workflowNumber);
    }

    @PostMapping("/{workflowNumber}/approve")
    public Workflow approveWorkflow(
            @PathVariable String workflowNumber,
            @Valid @RequestBody ApproveWorkflowRequest request) {
        return workflowService.approveWorkflow(workflowNumber, request);
    }

    @PostMapping("/{workflowNumber}/reject")
    public Workflow rejectWorkflow(
            @PathVariable String workflowNumber,
            @Valid @RequestBody RejectWorkflowRequest request) {
        return workflowService.rejectWorkflow(workflowNumber, request);
    }
}
