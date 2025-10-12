package com.example.demo.starter.application.service.ai;

import com.example.demo.starter.application.dto.meeting.MeetingDto;
import com.example.demo.starter.application.dto.pbi.ProductBacklogItemDto;
import com.example.demo.starter.infrastructure.common.response.ServiceResponse;

import java.util.List;
import java.util.UUID;

public interface AIService {
    ServiceResponse<List<ProductBacklogItemDto>> analyzeBacklog(MeetingDto meeting);
}
