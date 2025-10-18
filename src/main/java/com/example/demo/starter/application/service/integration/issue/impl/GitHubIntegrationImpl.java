package com.example.demo.starter.application.service.integration.issue.impl;

import com.example.demo.starter.application.dto.integration.RepositoryDto;
import com.example.demo.starter.application.service.auth.CustomUserDetailsService;
import com.example.demo.starter.application.service.integration.issue.GithubIntegration;
import com.example.demo.starter.application.service.integration.token.IntegrationService;
import com.example.demo.starter.domain.entity.ProductBacklogItem;
import com.example.demo.starter.domain.enumeration.ProviderType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubIntegrationImpl implements GithubIntegration {

    private final RestTemplate restTemplate = new RestTemplate();
    @Lazy
    private final IntegrationService integrationService;
    private final CustomUserDetailsService userService;

    @Override
    public void createIssue(ProductBacklogItem pbi, String repositoryId) {
        try {
            UUID userId = userService.getCurrentUserId();

            String githubToken = integrationService
                    .getDecryptedToken(userId, ProviderType.GITHUB)
                    .orElseThrow(() -> new IllegalStateException("No GitHub token found for user " + userId));

            HttpHeaders headers = setHeaders(githubToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            String repoUrl = String.format("https://api.github.com/repositories/%s", repositoryId);
            ResponseEntity<Map> repoResponse = restTemplate.exchange(repoUrl, HttpMethod.GET, entity, Map.class);

            if (!repoResponse.getStatusCode().is2xxSuccessful() || repoResponse.getBody() == null)
                throw new IllegalStateException("Failed to fetch repository info for ID: " + repositoryId);

            Map<String, Object> repoData = repoResponse.getBody();
            String fullName = (String) repoData.get("full_name");
            log.info("Resolved repository full name: {}", fullName);

            String url = String.format("https://api.github.com/repos/%s/issues", fullName);

            StringBuilder bodyBuilder = new StringBuilder();
            bodyBuilder.append(buildDescription(pbi));

            bodyBuilder.append("\n\n> Hey [@copilot](https://github.com/copilot), please start working on this task.")
                    .append("\n> Make sure to follow our project conventions and open a PR when done.");

            String bodyText = bodyBuilder.toString();

            Map<String, Object> body = new HashMap<>();
            body.put("title", pbi.getTitle());
            body.put("body", bodyText);
            body.put("labels", List.of("auto-assign-copilot"));

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
    public List<RepositoryDto> getRepositories(String accessToken, UUID userId) {
        String githubToken = integrationService
                .getDecryptedToken(userId, ProviderType.GITHUB)
                .orElseThrow(() -> new IllegalStateException("No GitHub token found for user " + userId));
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String githubReposUrl = "https://api.github.com/user/repos?per_page=100";
        ResponseEntity<RepositoryDto[]> response = restTemplate.exchange(
                githubReposUrl,
                HttpMethod.GET,
                entity,
                RepositoryDto[].class
        );

        return Arrays.asList(response.getBody());
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

    @Override
    public Optional<Map<String, String>> getUserInfo(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response =
                    restTemplate.exchange("https://api.github.com/user", HttpMethod.GET, entity, Map.class);

            if (!response.getStatusCode().is2xxSuccessful()) return Optional.empty();

            Map<String, Object> body = response.getBody();
            Map<String, String> info = Map.of(
                    "username", (String) body.get("login"),
                    "email", body.get("email") != null ? (String) body.get("email") : ""
            );
            return Optional.of(info);

        } catch (Exception e) {
            log.error("Error fetching GitHub user info: {}", e.getMessage());
            return Optional.empty();
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

    private String buildDescription(ProductBacklogItem pbi) {
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
