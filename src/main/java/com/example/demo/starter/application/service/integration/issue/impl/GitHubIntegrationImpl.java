package com.example.demo.starter.application.service.integration.issue.impl;

import com.example.demo.starter.application.dto.pbi.ProductBacklogItemDto;
import com.example.demo.starter.application.service.auth.CustomUserDetailsService;
import com.example.demo.starter.application.service.integration.issue.GithubIntegration;
import com.example.demo.starter.application.service.integration.token.IntegrationService;
import com.example.demo.starter.domain.enumeration.ProviderType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubIntegrationImpl implements GithubIntegration {

    private final RestTemplate restTemplate = new RestTemplate();
    private final IntegrationService integrationService;
    private final CustomUserDetailsService userService;

    @Value("${github.repo.owner}")
    private String repoOwner;

    @Value("${github.repo.name}")
    private String repoName;

    @Override
    public void createIssue(ProductBacklogItemDto pbi) {
        try {
            UUID userId = userService.getCurrentUserId();

            String githubToken = integrationService
                    .getDecryptedToken(userId, ProviderType.GITHUB)
                    .orElseThrow(() -> new IllegalStateException("No GitHub token found for user " + userId));

            boolean copilotAvailable = checkIfCopilotExists(githubToken);

            String url = String.format("https://api.github.com/repos/%s/%s/issues", repoOwner, repoName);
            HttpHeaders headers = setHeaders(githubToken);
            String bodyText = buildDescription(pbi);

            Map<String, Object> body;
            if (copilotAvailable) {
                bodyText += "\n\n> Hey @copilot, please start working on this task.\n"
                        + "> Make sure to follow our project conventions and open a PR when done.";

                body = Map.of("title", pbi.getTitle(), "body", bodyText, "assignees", List.of("copilot"));
                log.info("Copilot detected. Assigning issue to @copilot.");
            } else {
                body = Map.of("title", pbi.getTitle(), "body", bodyText);
                log.info("Copilot not available. Creating regular issue only.");
            }

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("GitHub issue created successfully: {}", response.getBody().get("html_url"));
            } else {
                log.error("GitHub issue creation failed: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error creating GitHub issue: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create GitHub issue", e);
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            ResponseEntity<Map> res = new RestTemplate().exchange(
                    "https://api.github.com/user",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map.class
            );
            return res.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    private HttpHeaders setHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private boolean checkIfCopilotExists(String token) {
        try {
            String url = "https://api.github.com/users/copilot";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Copilot user not found — agent mode probably disabled for this repo.");
            return false;
        } catch (Exception e) {
            log.error("Error while checking Copilot availability: {}", e.getMessage());
            return false;
        }
    }

    private String buildDescription(ProductBacklogItemDto pbi) {
        StringBuilder sb = new StringBuilder();
        sb.append("**Description:**\n")
                .append(pbi.getDescription())
                .append("\n\n")
                .append("**Acceptance Criteria:**\n")
                .append(pbi.getAcceptanceCriteria())
                .append("\n\n")
                .append("**Priority:** ").append(pbi.getPriority() != null ? pbi.getPriority() : "Normal")
                .append("\n")
                .append("**Created by:** ").append("System:TODO-CREATED-BY");
        return sb.toString();
    }

    @Override
    public String getProviderName() {
        return "GITHUB";
    }
}
