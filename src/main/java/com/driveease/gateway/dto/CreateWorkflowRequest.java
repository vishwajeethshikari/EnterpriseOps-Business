
package com.driveease.gateway.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record CreateWorkflowRequest(
    @NotBlank
    @Pattern(regexp = "ORD-[0-9]{4,}")
    String orderNumber,

    @NotBlank
    @Size(min = 5, max = 2000)
    String issueDescription,

    @NotBlank
    @Pattern(regexp = "LOW|MEDIUM|HIGH")
    String priority
) {}
