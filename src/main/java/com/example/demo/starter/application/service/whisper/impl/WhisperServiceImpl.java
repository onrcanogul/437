package com.example.demo.starter.application.service.whisper.impl;


import com.example.demo.starter.application.service.whisper.WhisperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhisperServiceImpl implements WhisperService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Downloads the audio file from the given URL,
     * sends it to the Whisper API for transcription,
     * and returns the resulting text.
     */
    public String transcribeFromUrl(String s3Url) {
        try (InputStream inputStream = new URL(s3Url).openStream()) {
            byte[] audioBytes = StreamUtils.copyToByteArray(inputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(audioBytes) {
                @Override
                public String getFilename() {
                    return "audio.mp3";
                }
            });
            body.add("model", "whisper-1");

            HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.openai.com/v1/audio/transcriptions",
                    requestEntity,
                    Map.class
            );

            return (String) response.getBody().get("text");

        } catch (Exception e) {
            log.error("Failed to transcribe audio: {}", e.getMessage(), e);
            throw new RuntimeException("Whisper transcription failed", e);
        }
    }
}
