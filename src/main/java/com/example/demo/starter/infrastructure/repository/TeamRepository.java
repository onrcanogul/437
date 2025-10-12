package com.example.demo.starter.infrastructure.repository;

import com.example.demo.starter.domain.entity.Team;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamRepository extends BaseRepository<Team> {
    @Query("select t from Team t join t.members p where p.id = :memberId")
    List<Team> findByParticipant(@Param("memberId") UUID memberId);
}
