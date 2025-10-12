package com.example.demo.starter.presentation.controller.v1;

import com.example.demo.starter.application.dto.team.TeamDto;
import com.example.demo.starter.application.service.team.TeamService;
import com.example.demo.starter.infrastructure.common.response.NoContent;
import com.example.demo.starter.infrastructure.common.response.ServiceResponse;
import com.example.demo.starter.presentation.controller.base.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/team")
public class TeamController extends BaseController {
    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    @Operation(summary = "-TEST- Get All Teams")
    public ResponseEntity<ServiceResponse<List<TeamDto>>> get() {
        return controllerResponse(teamService.get(0, 0));
    }

    @GetMapping("member/{memberId}")
    @Operation(summary = "Get Team By Id")
    public ResponseEntity<ServiceResponse<List<TeamDto>>> getByMember(@PathVariable UUID memberId) {
        return controllerResponse(teamService.getByMember(memberId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Team By Id")
    public ResponseEntity<ServiceResponse<TeamDto>> get(@PathVariable UUID id) {
        return controllerResponse(teamService.getSingle(id));
    }

    @PostMapping
    @Operation(summary = "-TEST- Create Team")
    public ResponseEntity<ServiceResponse<TeamDto>> create(TeamDto model) {
        return controllerResponse(teamService.create(model));
    }

    @PutMapping
    @Operation(summary = "-TEST- Update Team")
    public ResponseEntity<ServiceResponse<TeamDto>> update(TeamDto model) {
        return controllerResponse(teamService.update(model, model.getId()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "-TEST- Delete Team")
    public ResponseEntity<ServiceResponse<NoContent>> update(@PathVariable UUID id) {
        return controllerResponse(teamService.delete(id));
    }
}
