package com.example.demo.starter.application.dto.meeting;

import com.example.demo.starter.application.dto.base.BaseDto;
import com.example.demo.starter.application.dto.pbi.ProductBacklogItemDto;
import com.example.demo.starter.domain.entity.User;
import com.example.demo.starter.domain.enumeration.MeetingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class MeetingDto extends BaseDto {
    private String title;
    private String transcript;
    private MeetingStatus status = MeetingStatus.UPLOADED;
    private User user;
    private List<ProductBacklogItemDto> backlogItems = new ArrayList<>();
}
