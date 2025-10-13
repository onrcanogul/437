package com.example.demo.starter.application.service.integration.issue.impl;

import com.example.demo.starter.application.dto.pbi.ProductBacklogItemDto;
import com.example.demo.starter.application.service.integration.issue.AzureIntegration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AzureIntegrationImpl implements AzureIntegration {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${azure.organization}")
    private String organization;

    @Value("${azure.project}")
    private String project;

    @Value("${azure.pat}")
    private String personalAccessToken;


    @Override
    public void createIssue(ProductBacklogItemDto pbi) {
        try {
            String url = String.format(
                    "https://dev.azure.com/%s/%s/_apis/wit/workitems/$Product%%20Backlog%%20Item?api-version=7.0",
                    organization, project
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth("", personalAccessToken);
            headers.setContentType(MediaType.valueOf("application/json-patch+json"));

            List<Map<String, Object>> body = List.of(
                    Map.of("op", "add", "path", "/fields/System.Title", "value", pbi.getTitle()),
                    Map.of("op", "add", "path", "/fields/System.Description", "value", formatAsHtml(pbi.getDescription())),
                    Map.of("op", "add", "path", "/fields/Microsoft.VSTS.Common.FunctionalAnalysis", "value",
                            formatAsHtml(generateFunctionalAnalysis(pbi))),
                    Map.of("op", "add", "path", "/fields/Microsoft.VSTS.Common.TechnicalAnalysis", "value",
                            formatAsHtml(generateTechnicalAnalysis(pbi))),
                    Map.of("op", "add", "path", "/fields/Microsoft.VSTS.Common.AcceptanceCriteria", "value",
                            formatAsHtml(pbi.getAcceptanceCriteria())),
                    Map.of("op", "add", "path", "/fields/Microsoft.VSTS.Common.Priority", "value", mapPriority(pbi.getPriority()))
            );

            HttpEntity<List<Map<String, Object>>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Azure DevOps PBI created successfully.");
            } else {
                log.error("Azure DevOps PBI creation failed: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error creating Azure DevOps PBI: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create Azure DevOps PBI", e);
        }
    }

    @Override
    public boolean validateToken(String token) {
        return true;
    }

    @Override
    public String getProviderName() {
        return "AZURE";
    }

    private String formatAsHtml(String text) {
        if (text == null) return "";
        return "<p>" + text.replace("\n", "<br>") + "</p>";
    }

    private int mapPriority(Enum<?> priority) {
        if (priority == null) return 2; // Medium by default
        return switch (priority.name()) {
            case "HIGH" -> 1;
            case "LOW" -> 3;
            default -> 2;
        };
    }

    private String generateFunctionalAnalysis(ProductBacklogItemDto pbi) {
        return """
            <strong>Functional Overview:</strong><br>
            %s<br><br>
            <strong>User Need:</strong><br>
            The system should support the described feature based on the meeting discussion.
            """.formatted(pbi.getDescription());
    }

    private String generateTechnicalAnalysis(ProductBacklogItemDto pbi) {
        return """
            <strong>Technical Considerations:</strong><br>
            - Expected complexity: %s<br>
            - Requires integration with existing modules<br>
            - Implementation should follow coding and security standards.
            """.formatted(pbi.getPriority() != null ? pbi.getPriority().name() : "MEDIUM");
    }
}


