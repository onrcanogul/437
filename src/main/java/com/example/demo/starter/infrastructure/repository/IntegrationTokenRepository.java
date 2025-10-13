package com.example.demo.starter.infrastructure.repository;

import com.example.demo.starter.domain.entity.IntegrationToken;
import com.example.demo.starter.domain.enumeration.ProviderType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IntegrationTokenRepository extends BaseRepository<IntegrationToken> {
    List<IntegrationToken> findByUserId(UUID userId);
    Optional<IntegrationToken> findByProviderAndUserId(ProviderType provider, UUID userId);
}
