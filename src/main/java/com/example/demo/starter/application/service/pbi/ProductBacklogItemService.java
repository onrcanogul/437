package com.example.demo.starter.application.service.pbi;

import com.example.demo.starter.application.dto.meeting.MeetingDto;
import com.example.demo.starter.application.dto.pbi.ProductBacklogItemDto;
import com.example.demo.starter.application.service.base.BaseService;
import com.example.demo.starter.domain.entity.ProductBacklogItem;
import com.example.demo.starter.infrastructure.common.response.ServiceResponse;

import java.util.List;

public interface ProductBacklogItemService extends BaseService<ProductBacklogItem, ProductBacklogItemDto> {
    ServiceResponse<List<ProductBacklogItemDto>> analyzeAndCreate(MeetingDto meeting);
}
