package com.example.demo.starter.application.dto.meeting;

import com.example.demo.starter.application.dto.base.BaseDto;
import com.example.demo.starter.application.dto.pbi.ProductBacklogItemDto;
import com.example.demo.starter.application.dto.user.UserDto;
import com.example.demo.starter.domain.enumeration.MeetingStatus;
import com.example.demo.starter.domain.enumeration.ProviderType;
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
    private UserDto user;
    private String repositoryId;
    private ProviderType repositoryProvider;
    private List<ProductBacklogItemDto> backlogItems = new ArrayList<>();
}
