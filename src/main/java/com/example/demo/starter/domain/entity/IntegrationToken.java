package com.example.demo.starter.domain.entity;

import com.example.demo.starter.domain.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "integration_tokens")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class IntegrationToken extends BaseEntity {
    private String provider; // "AZURE", "JIRA"

    private String token; // encrypted

    private String meta; // project name etc.

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
