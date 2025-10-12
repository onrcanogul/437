package com.example.demo.starter.domain.entity;

import com.example.demo.starter.domain.entity.base.BaseEntity;
import com.example.demo.starter.domain.enumeration.PbiStatus;
import com.example.demo.starter.domain.enumeration.Priority;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_backlog_items")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ProductBacklogItem extends BaseEntity {
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    private PbiStatus status = PbiStatus.DRAFT;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String acceptanceCriteria;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Meeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}

