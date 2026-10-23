
package com.driveease.gateway.dto;

public class CreateTicketRequest {

    private String orderNumber;
    private String issueDescription;
    private String priority;

    public CreateTicketRequest() {}

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
}
