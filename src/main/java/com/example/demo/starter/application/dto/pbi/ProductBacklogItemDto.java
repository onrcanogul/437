package com.example.demo.starter.application.dto.pbi;

import com.example.demo.starter.application.dto.base.BaseDto;
import com.example.demo.starter.application.dto.meeting.MeetingDto;
import com.example.demo.starter.application.dto.user.UserDto;
import com.example.demo.starter.domain.enumeration.PbiStatus;
import com.example.demo.starter.domain.enumeration.Priority;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ProductBacklogItemDto extends BaseDto {
    private String title;
    private String description;
    private Priority priority = Priority.MEDIUM;
    private PbiStatus status = PbiStatus.DRAFT;
    private String acceptanceCriteria;
    @JsonIgnore
    private MeetingDto meeting;
    private UserDto user;
}
