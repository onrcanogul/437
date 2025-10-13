package com.example.demo.starter.domain.entity;

import com.example.demo.starter.domain.entity.base.BaseEntity;
import com.example.demo.starter.domain.enumeration.ProviderType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "integration_tokens")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class IntegrationToken extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private ProviderType provider;

    private String token; // encrypted

    private String meta; // project name etc.
    private String usernameAtProvider;
    private String emailAtProvider;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
