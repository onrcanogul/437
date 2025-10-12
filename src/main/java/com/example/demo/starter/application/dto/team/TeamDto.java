package com.example.demo.starter.application.dto.team;

import com.example.demo.starter.application.dto.base.BaseDto;
import com.example.demo.starter.application.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class TeamDto extends BaseDto {
    private String name;
    private String description;
    private List<UserDto> members = new ArrayList<>();
}
