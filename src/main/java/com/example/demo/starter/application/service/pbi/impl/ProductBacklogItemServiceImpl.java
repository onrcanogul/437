package com.example.demo.starter.application.service.pbi.impl;

import com.example.demo.starter.application.dto.meeting.MeetingDto;
import com.example.demo.starter.application.dto.pbi.ProductBacklogItemDto;
import com.example.demo.starter.application.service.ai.AIService;
import com.example.demo.starter.application.service.base.impl.BaseServiceImpl;
import com.example.demo.starter.application.service.pbi.ProductBacklogItemService;
import com.example.demo.starter.domain.entity.ProductBacklogItem;
import com.example.demo.starter.infrastructure.common.response.ServiceResponse;
import com.example.demo.starter.infrastructure.configuration.mapper.Mapper;
import com.example.demo.starter.infrastructure.repository.BaseRepository;
import com.example.demo.starter.infrastructure.repository.ProductBacklogItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductBacklogItemServiceImpl extends BaseServiceImpl<ProductBacklogItem, ProductBacklogItemDto> implements ProductBacklogItemService {
    private final Mapper<ProductBacklogItem, ProductBacklogItemDto> mapper;
    private final ProductBacklogItemRepository repository;
    private final AIService aiService;
    public ProductBacklogItemServiceImpl(ProductBacklogItemRepository repository, Mapper<ProductBacklogItem, ProductBacklogItemDto> mapper, AIService aiService) {
        super(repository, mapper);
        this.mapper = mapper;
        this.repository = repository;
        this.aiService = aiService;
    }

    @Override
    public ServiceResponse<List<ProductBacklogItemDto>> getByMeeting(UUID meetingId) {
        List<ProductBacklogItem> backlogItems = repository.findByMeeting_Id(meetingId);
        List<ProductBacklogItemDto> dto = backlogItems.stream().map(mapper::toDto).toList();
        return ServiceResponse.success(dto, 200);
    }

    @Override
    public ServiceResponse<List<ProductBacklogItemDto>> analyzeAndCreate(MeetingDto meeting) {
        List<ProductBacklogItemDto> backlogItems = aiService.analyzeBacklog(meeting).getData();
        backlogItems.forEach(x -> x.setMeeting(meeting));
        List<ProductBacklogItem> items = repository.saveAll(backlogItems.stream().map(mapper::toEntity).toList());
        return ServiceResponse.success(items.stream().map(mapper::toDto).toList(), 200);
    }


    @Override
    protected void updateEntity(ProductBacklogItemDto dto, ProductBacklogItem entity) {
        entity = mapper.toEntity(dto);
    }
}
