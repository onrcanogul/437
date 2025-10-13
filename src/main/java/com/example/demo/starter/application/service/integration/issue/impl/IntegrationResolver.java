package com.example.demo.starter.application.service.integration.issue.impl;

import com.example.demo.starter.application.service.integration.issue.IssueIntegration;
import com.example.demo.starter.domain.enumeration.ProviderType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class IntegrationResolver {

    private final ApplicationContext context;

    public IssueIntegration resolve(ProviderType provider) {
        Map<String, IssueIntegration> beans = context.getBeansOfType(IssueIntegration.class);
        return beans.values().stream()
                .filter(i -> i.getProviderName().equalsIgnoreCase(provider.toString()))
                .findFirst()
                .orElseThrow();
    }
}