package com.example.demo.starter.application.service.integration.token.impl;

import com.example.demo.starter.application.dto.integration.IntegrationTokenDto;
import com.example.demo.starter.application.service.auth.CustomUserDetailsService;
import com.example.demo.starter.application.service.integration.issue.IssueIntegration;
import com.example.demo.starter.application.service.integration.issue.impl.IntegrationResolver;
import com.example.demo.starter.application.service.integration.token.IntegrationService;
import com.example.demo.starter.domain.entity.IntegrationToken;
import com.example.demo.starter.domain.entity.User;
import com.example.demo.starter.domain.enumeration.ProviderType;
import com.example.demo.starter.infrastructure.configuration.mapper.Mapper;
import com.example.demo.starter.infrastructure.exception.BadRequestException;
import com.example.demo.starter.infrastructure.exception.NotFoundException;
import com.example.demo.starter.infrastructure.repository.IntegrationTokenRepository;
import com.example.demo.starter.infrastructure.repository.UserRepository;
import com.example.demo.starter.infrastructure.util.encryptor.AesEncryptor;
import com.example.demo.starter.infrastructure.util.response.NoContent;
import com.example.demo.starter.infrastructure.util.response.ServiceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class IntegrationServiceImpl implements IntegrationService {
    private final UserRepository userRepository;
    private final IntegrationTokenRepository repository;
    private final AesEncryptor encryptor;
    private final CustomUserDetailsService userService;
    private final IntegrationResolver integrationResolver;
    private final Mapper<IntegrationToken, IntegrationTokenDto> mapper;

    public IntegrationServiceImpl(UserRepository userRepository, IntegrationTokenRepository repository, AesEncryptor encryptor, CustomUserDetailsService userService, IntegrationResolver integrationResolver, Mapper<IntegrationToken, IntegrationTokenDto> mapper) {
        this.userRepository = userRepository;
        this.repository = repository;
        this.encryptor = encryptor;
        this.userService = userService;
        this.integrationResolver = integrationResolver;
        this.mapper = mapper;
    }

    @Override
    public ServiceResponse<NoContent> connectUser(ProviderType provider, String token, String meta) {
        IssueIntegration resolvedIntegration = integrationResolver.resolve(provider);
        if(!resolvedIntegration.validateToken(token)) throw new BadRequestException("Token is not valid");

        String encrypted = encryptor.encrypt(token);
        User user = userRepository.findById(userService.getCurrentUserId()).orElseThrow(
                () -> new NotFoundException("User Not Found")
        );

        IntegrationToken integration = IntegrationToken.builder()
                .provider(provider)
                .token(encrypted)
                .meta(meta)
                .user(user)
                .build();

        repository.save(integration);
        return ServiceResponse.success(204);
    }

    @Override
    public Optional<String> getDecryptedToken(UUID userId, ProviderType provider) {
        return repository.findByProviderAndUserId(provider, userId)
                .map(t -> encryptor.decrypt(t.getToken()));
    }
}
