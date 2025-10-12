package com.example.demo.starter.application.service.team;

import com.example.demo.starter.application.dto.team.TeamDto;
import com.example.demo.starter.application.service.base.BaseService;
import com.example.demo.starter.domain.entity.Team;
import com.example.demo.starter.infrastructure.common.response.ServiceResponse;

import java.util.List;
import java.util.UUID;

public interface TeamService extends BaseService<Team, TeamDto> {
    ServiceResponse<List<TeamDto>> getByMember(UUID memberId);
}
