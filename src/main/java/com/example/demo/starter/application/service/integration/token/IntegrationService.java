package com.example.demo.starter.application.service.integration.token;

import com.example.demo.starter.domain.enumeration.ProviderType;
import com.example.demo.starter.infrastructure.util.response.NoContent;
import com.example.demo.starter.infrastructure.util.response.ServiceResponse;

import java.util.Optional;
import java.util.UUID;

public interface IntegrationService {
    ServiceResponse<NoContent> connectUser(ProviderType provider, String token, String meta);
    Optional<String> getDecryptedToken(UUID userId, ProviderType provider);
}
