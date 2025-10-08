package com.example.demo.starter.application.service.meeting.impl;

import com.example.demo.starter.application.dto.meeting.MeetingDto;
import com.example.demo.starter.application.service.base.impl.BaseServiceImpl;
import com.example.demo.starter.application.service.meeting.MeetingService;
import com.example.demo.starter.domain.entity.Meeting;
import com.example.demo.starter.infrastructure.configuration.mapper.Mapper;
import com.example.demo.starter.infrastructure.repository.BaseRepository;
import org.springframework.stereotype.Service;

@Service
public class MeetingServiceImpl extends BaseServiceImpl<Meeting, MeetingDto> implements MeetingService {
    public MeetingServiceImpl(BaseRepository<Meeting> repository, Mapper<Meeting, MeetingDto> mapper) {
        super(repository, mapper);
    }

    @Override
    protected void updateEntity(MeetingDto dto, Meeting entity) {

    }
}
