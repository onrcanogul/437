package com.example.demo.starter.infrastructure.repository;

import com.example.demo.starter.domain.entity.base.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BaseRepository<D extends BaseEntity> extends JpaRepository<D, UUID> {
}
