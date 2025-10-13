package com.example.demo.starter.application.service.integration.issue.impl;

import com.example.demo.starter.application.service.integration.issue.IssueIntegration;
import com.example.demo.starter.domain.enumeration.ProviderType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IntegrationResolver {

    private final List<IssueIntegration> integrations;

    public IssueIntegration resolve(ProviderType provider) {
        return integrations.stream()
                .filter(i -> i.getProviderName().equalsIgnoreCase(provider.name()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported provider: " + provider));
    }
}