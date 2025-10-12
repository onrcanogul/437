package com.example.demo.starter.domain.entity;

import com.example.demo.starter.domain.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "teams")
@NoArgsConstructor @AllArgsConstructor
@SQLRestriction("is_deleted = false")
public class Team extends BaseEntity {
    private String name;
    private String description;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "team_participants",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private List<User> members = new ArrayList<>();
}
