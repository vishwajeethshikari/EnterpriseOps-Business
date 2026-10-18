
package com.driveease.gateway.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectWorkflowRequest(
    @NotBlank
    @Size(max = 100)
    String rejectedBy,

    @NotBlank
    @Size(min = 3, max = 1000)
    String reason
) {}
