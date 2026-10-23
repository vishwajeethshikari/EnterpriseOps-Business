
package com.driveease.gateway.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ApproveWorkflowRequest(
        @NotBlank
        @Size(max = 100)
        String approvedBy,

        @NotBlank
        @Size(min = 3, max = 1000)
        String reason
) {}
