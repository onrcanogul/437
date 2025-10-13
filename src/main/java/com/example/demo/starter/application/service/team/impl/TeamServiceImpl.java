package com.example.demo.starter.application.service.team.impl;

import com.example.demo.starter.application.dto.team.TeamDto;
import com.example.demo.starter.application.dto.user.UserDto;
import com.example.demo.starter.application.service.base.impl.BaseServiceImpl;
import com.example.demo.starter.application.service.team.TeamService;
import com.example.demo.starter.domain.entity.Team;
import com.example.demo.starter.domain.entity.User;
import com.example.demo.starter.infrastructure.util.response.ServiceResponse;
import com.example.demo.starter.infrastructure.configuration.mapper.Mapper;
import com.example.demo.starter.infrastructure.exception.NotFoundException;
import com.example.demo.starter.infrastructure.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TeamServiceImpl extends BaseServiceImpl<Team, TeamDto> implements TeamService {
    private final TeamRepository repository;
    private final Mapper<Team, TeamDto> mapper;
    private final Mapper<User, UserDto> userMapper;
    public TeamServiceImpl(TeamRepository repository, Mapper<Team, TeamDto> mapper, Mapper<User, UserDto> userMapper) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse<List<TeamDto>> getByMember(UUID memberId) {
        List<Team> teams = repository.findByParticipant(memberId);
        List<TeamDto> dtoList = teams.stream().map(mapper::toDto).toList();
        return ServiceResponse.success(dtoList, 200);
    }

    @Override
    @Transactional
    public ServiceResponse<TeamDto> addTeamMember(UUID teamId, List<UserDto> users) {
        Team team = repository.findById(teamId)
                .orElseThrow(
                        () -> new NotFoundException("Team Not Found")
                );

        team.setMembers(users.stream().map(userMapper::toEntity).toList());
        Team savedTeam = repository.save(team);

        return ServiceResponse.success(mapper.toDto(savedTeam), 200);
    }


    @Override
    protected void updateEntity(TeamDto dto, Team entity) {
        entity = mapper.toEntity(dto);
    }
}
