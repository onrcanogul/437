package com.example.demo.starter.application.service.pbi.impl;

import com.example.demo.starter.application.dto.meeting.MeetingDto;
import com.example.demo.starter.application.dto.pbi.ProductBacklogItemDto;
import com.example.demo.starter.application.service.ai.AIService;
import com.example.demo.starter.application.service.base.impl.BaseServiceImpl;
import com.example.demo.starter.application.service.integration.issue.IssueIntegration;
import com.example.demo.starter.application.service.integration.issue.impl.IntegrationResolver;
import com.example.demo.starter.application.service.pbi.ProductBacklogItemService;
import com.example.demo.starter.domain.entity.ProductBacklogItem;
import com.example.demo.starter.domain.enumeration.ProviderType;
import com.example.demo.starter.infrastructure.exception.BadRequestException;
import com.example.demo.starter.infrastructure.exception.NotFoundException;
import com.example.demo.starter.infrastructure.util.response.NoContent;
import com.example.demo.starter.infrastructure.util.response.ServiceResponse;
import com.example.demo.starter.infrastructure.configuration.mapper.Mapper;
import com.example.demo.starter.infrastructure.repository.ProductBacklogItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductBacklogItemServiceImpl extends BaseServiceImpl<ProductBacklogItem, ProductBacklogItemDto> implements ProductBacklogItemService {
    private final Mapper<ProductBacklogItem, ProductBacklogItemDto> mapper;
    private final ProductBacklogItemRepository repository;
    private final IntegrationResolver integrationResolver;
    private final AIService aiService;
    public ProductBacklogItemServiceImpl(ProductBacklogItemRepository repository, Mapper<ProductBacklogItem, ProductBacklogItemDto> mapper, IntegrationResolver integrationResolver, AIService aiService) {
        super(repository, mapper);
        this.mapper = mapper;
        this.repository = repository;
        this.integrationResolver = integrationResolver;
        this.aiService = aiService;
    }

    @Override
    public ServiceResponse<List<ProductBacklogItemDto>> getByMeeting(UUID meetingId) {
        List<ProductBacklogItem> backlogItems = repository.findByMeeting_Id(meetingId);
        List<ProductBacklogItemDto> dto = backlogItems.stream().map(mapper::toDto).toList();
        return ServiceResponse.success(dto, 200);
    }

    @Override
    public ServiceResponse<NoContent> send(UUID id, ProviderType providerType) {
        ProductBacklogItem productBacklogItem = repository.findById(id).orElseThrow(
                () -> new NotFoundException("PBI not found")
        );
        var meetingsProvider = productBacklogItem.getMeeting().getRepositoryProvider();
        if(!providerType.equals(productBacklogItem.getMeeting().getRepositoryProvider()))
            throw new BadRequestException("Provider did not implement");
        IssueIntegration service = integrationResolver.resolve(meetingsProvider);
        service.createIssue(productBacklogItem, productBacklogItem.getMeeting().getRepositoryId());
        return ServiceResponse.success(204);
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
