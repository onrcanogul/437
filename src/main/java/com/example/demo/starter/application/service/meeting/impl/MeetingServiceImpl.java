package com.example.demo.starter.application.service.meeting.impl;

import com.example.demo.starter.application.dto.integration.RepositoryDto;
import com.example.demo.starter.application.dto.meeting.MeetingDto;
import com.example.demo.starter.application.service.audio.AudioService;
import com.example.demo.starter.application.service.auth.CustomUserDetailsService;
import com.example.demo.starter.application.service.base.impl.BaseServiceImpl;
import com.example.demo.starter.application.service.integration.issue.impl.IntegrationResolver;
import com.example.demo.starter.application.service.meeting.MeetingService;
import com.example.demo.starter.application.service.pbi.ProductBacklogItemService;
import com.example.demo.starter.domain.entity.IntegrationToken;
import com.example.demo.starter.domain.entity.Meeting;
import com.example.demo.starter.domain.entity.Team;
import com.example.demo.starter.domain.enumeration.MeetingStatus;
import com.example.demo.starter.infrastructure.repository.IntegrationTokenRepository;
import com.example.demo.starter.infrastructure.util.response.ServiceResponse;
import com.example.demo.starter.infrastructure.configuration.mapper.Mapper;
import com.example.demo.starter.infrastructure.exception.NotFoundException;
import com.example.demo.starter.infrastructure.repository.MeetingRepository;
import com.example.demo.starter.infrastructure.repository.TeamRepository;
import com.example.demo.starter.infrastructure.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MeetingServiceImpl extends BaseServiceImpl<Meeting, MeetingDto> implements MeetingService {
    private final Mapper<Meeting, MeetingDto> mapper;
    private final MeetingRepository repository;
    private final AudioService audioService;
    private final ProductBacklogItemService productBacklogItemService;
    private final CustomUserDetailsService userService;
    private final TeamRepository teamRepository;
    private final IntegrationTokenRepository integrationTokenRepository;
    private final IntegrationResolver integrationResolver;

    public MeetingServiceImpl(MeetingRepository repository,
                              Mapper<Meeting, MeetingDto> mapper,
                              AudioService audioService, ProductBacklogItemService productBacklogItemService, CustomUserDetailsService userService, UserRepository userRepository,
                              TeamRepository teamRepository, IntegrationTokenRepository integrationTokenRepository, IntegrationResolver integrationResolver
    ) {
        super(repository, mapper);
        this.mapper = mapper;
        this.repository = repository;
        this.audioService = audioService;
        this.productBacklogItemService = productBacklogItemService;
        this.userService = userService;
        this.teamRepository = teamRepository;
        this.integrationTokenRepository = integrationTokenRepository;
        this.integrationResolver = integrationResolver;
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse<List<MeetingDto>> get() {
        var meetings = repository.findAllWithRelations();
        var dtoList = meetings.stream().map(a -> {
            var dto = mapper.toDto(a);
            dto.setTranscript("");
            return dto;
        }).toList();
        return ServiceResponse.success(dtoList, 200);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse<List<MeetingDto>> getByTeam() {
        UUID teamId = userService.getCurrentTeamId();
        List<Meeting> meetings = repository.findByTeam(teamId);
        List<MeetingDto> dtoList = meetings.stream().map(a -> {
            MeetingDto dto = mapper.toDto(a);
            dto.setTranscript("");
            return dto;
        }).toList();
        return ServiceResponse.success(dtoList, 200);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse<MeetingDto> getById(UUID id) {
        var meeting = repository.findByIdWithRelations(id).orElseThrow(
                () -> new NotFoundException("Meeting Not Found")
        );
        var dto = mapper.toDto(meeting);
        meeting.setTranscript("");
        return ServiceResponse.success(dto, 200);
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

    @Override
    @Transactional
    public ServiceResponse<MeetingDto> upload(String transcript, String title) {
        Team team = teamRepository.findById(userService.getCurrentTeamId())
                .orElseThrow(
                        () -> new NotFoundException("Team Not Found")
                );

        Meeting meeting = Meeting.builder()
                .title(title)
                .transcript(transcript)
                .status(MeetingStatus.UPLOADED)
                .team(team)
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
