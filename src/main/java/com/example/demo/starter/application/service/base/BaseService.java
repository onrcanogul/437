package com.example.demo.starter.application.service.base;

import com.example.demo.starter.application.dto.base.BaseDto;
import com.example.demo.starter.domain.entity.base.BaseEntity;
import com.example.demo.starter.infrastructure.util.response.NoContent;
import com.example.demo.starter.infrastructure.util.response.ServiceResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface BaseService<T extends BaseEntity, D extends BaseDto> {
    public ServiceResponse<List<D>> get(int page, int size);
    public ServiceResponse<D> getSingle(UUID id);
    public ServiceResponse<D> create(D dto);
    public ServiceResponse<D> update(D dto, UUID id);
    public ServiceResponse<NoContent> delete(UUID id);
}
