package com.example.demo.starter.application.service.meeting;

import com.example.demo.starter.application.dto.meeting.MeetingDto;
import com.example.demo.starter.application.service.base.BaseService;
import com.example.demo.starter.domain.entity.Meeting;
import com.example.demo.starter.infrastructure.common.response.ServiceResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MeetingService extends BaseService<Meeting, MeetingDto> {
    ServiceResponse<MeetingDto> upload(MultipartFile file, String title) throws IOException, InterruptedException;
    ServiceResponse<MeetingDto> upload(String transcript, String title);
}
