package com.example.demo.starter.application.service.integration.issue;

import com.example.demo.starter.application.dto.integration.RepositoryDto;
import com.example.demo.starter.application.dto.pbi.ProductBacklogItemDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface IssueIntegration {
    boolean validateToken(String token);
    Optional<Map<String, String>> getUserInfo(String token);
    List<RepositoryDto> getRepositories(String accessToken, UUID userId);
    void createIssue(ProductBacklogItemDto pbi);
    String getProviderName(); // e.g. "GITHUB", "AZURE_DEVOPS", "JIRA"
}
