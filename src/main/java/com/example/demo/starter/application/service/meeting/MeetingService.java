package com.example.demo.starter.application.service.meeting;

import com.example.demo.starter.application.dto.meeting.MeetingDto;
import com.example.demo.starter.application.service.base.BaseService;
import com.example.demo.starter.domain.entity.Meeting;
import com.example.demo.starter.domain.enumeration.ProviderType;
import com.example.demo.starter.infrastructure.util.response.ServiceResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface MeetingService extends BaseService<Meeting, MeetingDto> {
    ServiceResponse<List<MeetingDto>> get();
    ServiceResponse<List<MeetingDto>> getByTeam();
    ServiceResponse<MeetingDto> getById(UUID id);
    ServiceResponse<MeetingDto> upload(MultipartFile file, String title, String repositoryId, ProviderType providerType) throws IOException, InterruptedException;
    ServiceResponse<MeetingDto> updateRepository(UUID meetingId, String repositoryId, ProviderType providerType);
    ServiceResponse<MeetingDto> upload(String transcript, String title, String repositoryId, ProviderType providerType);
}
