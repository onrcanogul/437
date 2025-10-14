package com.example.demo.starter.application.service.integration.token;

import com.example.demo.starter.application.dto.integration.IntegrationTokenDto;
import com.example.demo.starter.application.dto.integration.RepositoryDto;
import com.example.demo.starter.domain.enumeration.ProviderType;
import com.example.demo.starter.infrastructure.util.response.NoContent;
import com.example.demo.starter.infrastructure.util.response.ServiceResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IntegrationService {
    ServiceResponse<List<IntegrationTokenDto>> getByUser(UUID userId);
    ServiceResponse<NoContent> connectUser(ProviderType provider, String token, String meta);
    ServiceResponse<List<RepositoryDto>> getRepositoriesForMeeting();
    Optional<String> getDecryptedToken(UUID userId, ProviderType provider);
    ServiceResponse<NoContent> delete(UUID userId, ProviderType provider);
}
