package com.example.demo.starter.application.service.meeting.impl;

import com.example.demo.starter.application.dto.meeting.MeetingDto;
import com.example.demo.starter.application.service.audio.AudioService;
import com.example.demo.starter.application.service.auth.CustomUserDetailsService;
import com.example.demo.starter.application.service.base.impl.BaseServiceImpl;
import com.example.demo.starter.application.service.meeting.MeetingService;
import com.example.demo.starter.application.service.pbi.ProductBacklogItemService;
import com.example.demo.starter.domain.entity.Meeting;
import com.example.demo.starter.domain.entity.User;
import com.example.demo.starter.domain.enumeration.MeetingStatus;
import com.example.demo.starter.infrastructure.common.response.ServiceResponse;
import com.example.demo.starter.infrastructure.configuration.mapper.Mapper;
import com.example.demo.starter.infrastructure.repository.MeetingRepository;
import com.example.demo.starter.infrastructure.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class MeetingServiceImpl extends BaseServiceImpl<Meeting, MeetingDto> implements MeetingService {
    private final Mapper<Meeting, MeetingDto> mapper;
    private final MeetingRepository repository;
    private final AudioService audioService;
    private final ProductBacklogItemService productBacklogItemService;
    private final CustomUserDetailsService userService;
    private final UserRepository userRepository;

    public MeetingServiceImpl(MeetingRepository repository,
                              Mapper<Meeting, MeetingDto> mapper,
                              AudioService audioService, ProductBacklogItemService productBacklogItemService, CustomUserDetailsService userService, UserRepository userRepository
    ) {
        super(repository, mapper);
        this.mapper = mapper;
        this.repository = repository;
        this.audioService = audioService;
        this.productBacklogItemService = productBacklogItemService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ServiceResponse<MeetingDto> upload(MultipartFile file, String title) throws IOException, InterruptedException {
        String transcript = audioService.processAudioAndTranscribe(file);
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


    @Transactional
    public ServiceResponse<MeetingDto> upload(String transcript, String title) {
        UUID userId = userService.getCurrentUserId();
        Meeting meeting = Meeting.builder()
                .title(title)
                .transcript(transcript)
                .status(MeetingStatus.UPLOADED)
                .user(userRepository.getReferenceById(userId))
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
