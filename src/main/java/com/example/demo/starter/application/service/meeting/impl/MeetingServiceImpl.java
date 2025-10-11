package com.example.demo.starter.application.service.meeting.impl;

import com.example.demo.starter.application.dto.meeting.MeetingDto;
import com.example.demo.starter.application.service.audio.AudioService;
import com.example.demo.starter.application.service.base.impl.BaseServiceImpl;
import com.example.demo.starter.application.service.meeting.MeetingService;
import com.example.demo.starter.application.service.pbi.ProductBacklogItemService;
import com.example.demo.starter.domain.entity.Meeting;
import com.example.demo.starter.domain.enumeration.MeetingStatus;
import com.example.demo.starter.infrastructure.common.response.ServiceResponse;
import com.example.demo.starter.infrastructure.configuration.mapper.Mapper;
import com.example.demo.starter.infrastructure.repository.MeetingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class MeetingServiceImpl extends BaseServiceImpl<Meeting, MeetingDto> implements MeetingService {
    private final Mapper<Meeting, MeetingDto> mapper;
    private final MeetingRepository repository;
    private final AudioService audioService;
    private final ProductBacklogItemService productBacklogItemService;
    public MeetingServiceImpl(MeetingRepository repository, Mapper<Meeting, MeetingDto> mapper, AudioService audioService, ProductBacklogItemService productBacklogItemService) {
        super(repository, mapper);
        this.mapper = mapper;
        this.repository = repository;
        this.audioService = audioService;
        this.productBacklogItemService = productBacklogItemService;
    }

    @Override
    @Transactional
    public ServiceResponse<MeetingDto> upload(MultipartFile file, String title) throws IOException, InterruptedException {
        String transcript = audioService.processAndUploadAudio(file);
        Meeting meeting = Meeting.builder()
                .title(title)
                .transcript(transcript)
                .status(MeetingStatus.UPLOADED)
                .build();

        Meeting createdMeeting = repository.save(meeting);
        MeetingDto dto = mapper.toDto(createdMeeting);
        dto.setBacklogItems(productBacklogItemService.analyzeAndCreate(dto).getData());

        return ServiceResponse.success(dto, 201);
    }

    @Override
    protected void updateEntity(MeetingDto dto, Meeting entity) {
        entity = mapper.toEntity(dto);
    }
}
