
package com.driveease.gateway.repository;

import com.driveease.gateway.entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkflowRepository extends JpaRepository<Workflow, Long> {

    Optional<Workflow> findByWorkflowNumber(String workflowNumber);
}
