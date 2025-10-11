package com.example.demo.starter.domain.entity;

import com.example.demo.starter.domain.entity.base.BaseEntity;
import com.example.demo.starter.domain.enumeration.MeetingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "meetings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meeting extends BaseEntity {
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String transcript;

    @Enumerated(EnumType.STRING)
    private MeetingStatus status = MeetingStatus.UPLOADED;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductBacklogItem> backlogItems = new ArrayList<>();
}
