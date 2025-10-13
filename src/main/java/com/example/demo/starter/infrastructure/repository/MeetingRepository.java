package com.example.demo.starter.infrastructure.repository;

import com.example.demo.starter.domain.entity.Meeting;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MeetingRepository extends BaseRepository<Meeting> {
    @EntityGraph(attributePaths = {"backlogItems", "team"})
    @Query("select m from Meeting m")
    List<Meeting> findAllWithRelations();

    @EntityGraph(attributePaths = {"backlogItems", "team"})
    @Query("select m from Meeting m where m.team.id = :teamId")
    List<Meeting> findByTeam(UUID teamId);

    @EntityGraph(attributePaths = {"backlogItems", "team"})
    @Query("select m from Meeting m where m.id = :id")
    Optional<Meeting> findByIdWithRelations(UUID id);
}
