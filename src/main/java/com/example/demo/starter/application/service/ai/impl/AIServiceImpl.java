package com.example.demo.starter.application.service.ai.impl;

import com.example.demo.starter.application.dto.meeting.MeetingDto;
import com.example.demo.starter.application.dto.pbi.ProductBacklogItemDto;
import com.example.demo.starter.application.service.ai.AIService;
import com.example.demo.starter.infrastructure.common.response.ServiceResponse;

import java.util.List;

public class AIServiceImpl implements AIService {

    @Override
    public ServiceResponse<List<ProductBacklogItemDto>> analyzeBacklog(MeetingDto meeting) {
        //analyze from ai
        return ServiceResponse.success(List.of(new ProductBacklogItemDto()), 200);
    }
}
