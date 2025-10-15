package com.example.demo.starter.infrastructure.repository;

import com.example.demo.starter.domain.entity.ProductBacklogItem;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductBacklogItemRepository extends BaseRepository<ProductBacklogItem> {
    List<ProductBacklogItem> findByMeeting_Id(UUID meetingId);
    @Override
    @EntityGraph(attributePaths = {"meeting"})
    Optional<ProductBacklogItem> findById(UUID id);
}
