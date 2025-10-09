package com.example.demo.starter.application.service.pbi.impl;

import com.example.demo.starter.application.dto.pbi.ProductBacklogItemDto;
import com.example.demo.starter.application.service.base.impl.BaseServiceImpl;
import com.example.demo.starter.application.service.pbi.ProductBacklogItemService;
import com.example.demo.starter.domain.entity.ProductBacklogItem;
import com.example.demo.starter.infrastructure.configuration.mapper.Mapper;
import com.example.demo.starter.infrastructure.repository.BaseRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductBacklogItemServiceImpl extends BaseServiceImpl<ProductBacklogItem, ProductBacklogItemDto> implements ProductBacklogItemService {
    private final Mapper<ProductBacklogItem, ProductBacklogItemDto> mapper;
    public ProductBacklogItemServiceImpl(BaseRepository<ProductBacklogItem> repository, Mapper<ProductBacklogItem, ProductBacklogItemDto> mapper) {
        super(repository, mapper);
        this.mapper = mapper;
    }

    @Override
    protected void updateEntity(ProductBacklogItemDto dto, ProductBacklogItem entity) {
        entity = mapper.toEntity(dto);
    }
}
