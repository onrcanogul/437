package com.example.demo.starter.presentation.model;

import com.example.demo.starter.domain.enumeration.ProviderType;

public record CreateWithTranscriptRequestModel (String transcript, String title, String repositoryId, ProviderType providerType) {
}
