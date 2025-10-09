package com.example.demo.starter.infrastructure.configuration.mapper;

import com.example.demo.starter.application.dto.base.BaseDto;
import com.example.demo.starter.application.dto.meeting.MeetingDto;
import com.example.demo.starter.application.dto.pbi.ProductBacklogItemDto;
import com.example.demo.starter.domain.entity.Meeting;
import com.example.demo.starter.domain.entity.ProductBacklogItem;
import com.example.demo.starter.domain.entity.base.BaseEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfiguration {
    @Bean
    public Mapper<BaseEntity, BaseDto> baseMapper() {
        return new Mapper<>(BaseEntity.class, BaseDto.class);
    }
    @Bean
    public Mapper<Meeting, MeetingDto> meetingMapper() {
        return new Mapper<>(Meeting.class, MeetingDto.class);
    }
    @Bean
    public Mapper<ProductBacklogItem, ProductBacklogItemDto> pbiMapper() { return new Mapper<>(ProductBacklogItem.class, ProductBacklogItemDto.class); }
}