package com.example.demo.starter.application.dto.integration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RepositoryDto {
    private String id;
    private String name;
    private String full_name;
    @JsonProperty("private")
    private boolean privateRepo;
    private String html_url;
    private String description;
    private String language;
}
