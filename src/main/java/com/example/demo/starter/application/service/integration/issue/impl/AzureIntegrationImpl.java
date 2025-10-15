package com.example.demo.starter.application.service.integration.issue.impl;

import com.example.demo.starter.application.dto.integration.RepositoryDto;
import com.example.demo.starter.application.dto.pbi.ProductBacklogItemDto;
import com.example.demo.starter.application.service.integration.issue.AzureIntegration;
import com.example.demo.starter.domain.entity.ProductBacklogItem;
import com.example.demo.starter.domain.enumeration.ProviderType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.*;

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

    private static final String AZURE_ORG_URL = "https://dev.azure.com";


    @Override
    public void createIssue(ProductBacklogItem pbi, String repositoryId) {
        try {
            String url = String.format(
                    AZURE_ORG_URL, "/%s/%s/_apis/wit/workitems/$Product%%20Backlog%%20Item?api-version=7.0",
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
    public List<RepositoryDto> getRepositories(String accessToken, UUID userId) {

        String azureApiUrl = "https://dev.azure.com";
        String organization = "myorganization"; // bunu dinamik hale getireceğiz

        String url = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/%s/_apis/git/repositories", azureApiUrl, organization))
                .queryParam("api-version", "7.0")
                .toUriString();

        String basicAuth = Base64.getEncoder()
                .encodeToString((":" + accessToken).getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + basicAuth);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map.class
        );

        List<Map<String, Object>> repos = (List<Map<String, Object>>) response.getBody().get("value");

        return repos.stream()
                .map(repo -> {
                    RepositoryDto dto = new RepositoryDto();
                    dto.setId(String.valueOf(repo.get("id")));
                    dto.setName((String) repo.get("name"));
                    dto.setFull_name((String) ((Map<?, ?>) repo.get("project")).get("name") + "/" + repo.get("name"));
                    dto.setHtml_url((String) repo.get("webUrl"));
                    dto.setDescription((String) repo.get("description"));
                    dto.setLanguage("N/A");
                    dto.setPrivateRepo(true);
                    return dto;
                })
                .toList();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            HttpHeaders headers = azureHeaders(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    AZURE_ORG_URL + "/_apis/profile/profiles/me?api-version=7.0",
                    HttpMethod.GET, entity, Map.class
            );

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.warn("Azure token validation failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<Map<String, String>> getUserInfo(String token) {
        try {
            HttpHeaders headers = azureHeaders(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    AZURE_ORG_URL + "/_apis/profile/profiles/me?api-version=7.0",
                    HttpMethod.GET, entity, Map.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                return Optional.empty();
            }

            Map<String, Object> body = response.getBody();
            Map<String, String> info = Map.of(
                    "username", (String) body.get("displayName"),
                    "email", (String) body.get("emailAddress")
            );

            return Optional.of(info);

        } catch (Exception e) {
            log.error("Error fetching Azure DevOps user info: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private HttpHeaders azureHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String auth = ":" + token;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedAuth);

        return headers;
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

    private String generateFunctionalAnalysis(ProductBacklogItem pbi) {
        return """
            <strong>Functional Overview:</strong><br>
            %s<br><br>
            <strong>User Need:</strong><br>
            The system should support the described feature based on the meeting discussion.
            """.formatted(pbi.getDescription());
    }

    private String generateTechnicalAnalysis(ProductBacklogItem pbi) {
        return """
            <strong>Technical Considerations:</strong><br>
            - Expected complexity: %s<br>
            - Requires integration with existing modules<br>
            - Implementation should follow coding and security standards.
            """.formatted(pbi.getPriority() != null ? pbi.getPriority().name() : "MEDIUM");
    }
}


