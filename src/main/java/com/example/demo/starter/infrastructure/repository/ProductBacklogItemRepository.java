package com.example.demo.starter.infrastructure.repository;

import com.example.demo.starter.domain.entity.ProductBacklogItem;

import java.util.List;
import java.util.UUID;

public interface ProductBacklogItemRepository extends BaseRepository<ProductBacklogItem> {
    List<ProductBacklogItem> findByMeeting_Id(UUID meetingId);
}
