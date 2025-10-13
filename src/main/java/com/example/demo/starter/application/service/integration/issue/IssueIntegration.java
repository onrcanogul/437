package com.example.demo.starter.application.service.integration.issue;

import com.example.demo.starter.application.dto.pbi.ProductBacklogItemDto;

import java.util.Map;
import java.util.Optional;

public interface IssueIntegration {
    boolean validateToken(String token);
    Optional<Map<String, String>> getUserInfo(String token);
    void createIssue(ProductBacklogItemDto pbi);
    String getProviderName(); // e.g. "GITHUB", "AZURE_DEVOPS", "JIRA"
}
