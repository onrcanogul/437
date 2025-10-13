package com.example.demo.starter.application.service.pbi;

import com.example.demo.starter.application.dto.meeting.MeetingDto;
import com.example.demo.starter.application.dto.pbi.ProductBacklogItemDto;
import com.example.demo.starter.application.service.base.BaseService;
import com.example.demo.starter.domain.entity.ProductBacklogItem;
import com.example.demo.starter.infrastructure.util.response.ServiceResponse;

import java.util.List;
import java.util.UUID;

public interface ProductBacklogItemService extends BaseService<ProductBacklogItem, ProductBacklogItemDto> {
    ServiceResponse<List<ProductBacklogItemDto>> getByMeeting(UUID meetingId);
    ServiceResponse<List<ProductBacklogItemDto>> analyzeAndCreate(MeetingDto meeting);
}
