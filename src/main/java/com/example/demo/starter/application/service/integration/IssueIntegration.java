package com.example.demo.starter.application.service.integration;

import com.example.demo.starter.application.dto.pbi.ProductBacklogItemDto;

public interface IssueIntegration {
    void createIssue(ProductBacklogItemDto pbi);
    String getProviderName(); // e.g. "GITHUB", "AZURE_DEVOPS", "JIRA"
}
