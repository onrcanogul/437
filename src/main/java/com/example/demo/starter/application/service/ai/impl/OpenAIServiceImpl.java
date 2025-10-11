package com.example.demo.starter.application.service.ai.impl;

import com.example.demo.starter.application.dto.meeting.MeetingDto;
import com.example.demo.starter.application.dto.pbi.ProductBacklogItemDto;
import com.example.demo.starter.application.service.ai.OpenAIService;
import com.example.demo.starter.infrastructure.common.response.ServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenAIServiceImpl implements OpenAIService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.api.url}")
    private String openAiApiUrl;

    @Override
    public ServiceResponse<List<ProductBacklogItemDto>> analyzeBacklog(MeetingDto meeting) {
        try {
            String prompt = buildPrompt(meeting);

            Map<String, Object> requestBody = getBody(prompt);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(openAiApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(openAiApiUrl, entity, Map.class);
            String content = extractContent(response.getBody());

            List<ProductBacklogItemDto> items = parseItems(content);

            return ServiceResponse.success(items, 200);
        } catch (Exception ex) {
        log.error("Error while calling OpenAI API", ex);
        return ServiceResponse.failure("Error while prompting: " + ex.getMessage(), 500);
        }
    }

    private String buildPrompt(MeetingDto meeting) {
        try {
            String meetingJson = objectMapper.writeValueAsString(Map.of(
                    "meeting", Map.of(
                            "title", meeting.getTitle(),
                            "status", meeting.getStatus().name(),
                            "user", Map.of(
                                    "id", meeting.getUser().getId(),
                                    "username", meeting.getUser().getUsername()
                            ),
                            "transcript", meeting.getTranscript()
                    )
            ));

            return """
            You are an AI assistant that analyzes software development meetings
            and extracts backlog items in JSON format.

            The following meeting information is provided:
            %s

            Please extract all actionable Product Backlog Items discussed in the meeting
            as a valid JSON array of objects, each having the following fields:
            - title
            - description
            - priority (HIGH, MEDIUM, or LOW)
            - acceptanceCriteria

            Return only a valid JSON array.
            Do not include any explanation or markdown code block.
            """.formatted(meetingJson);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize meeting object", e);
        }
    }


    private Map<String, Object> getBody(String prompt) {
        return Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "system", "content", "You are an assistant that extracts backlog items in JSON format."),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.3
        );
    }

    @SuppressWarnings("unchecked")
    private String extractContent(Map<String, Object> body) {
        List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
        return (String) ((Map<String, Object>) choices.getFirst().get("message")).get("content");
    }

    private List<ProductBacklogItemDto> parseItems(String json) throws Exception {
        String cleaned = json
                .replaceAll("(?s)```json", "")
                .replaceAll("```", "")
                .trim();
        return objectMapper.readValue(cleaned, new TypeReference<>() {});
    }
}

