package com.example.demo.starter.presentation.controller.v1;

import com.example.demo.starter.application.dto.meeting.MeetingDto;
import com.example.demo.starter.application.service.meeting.MeetingService;
import com.example.demo.starter.infrastructure.common.response.NoContent;
import com.example.demo.starter.infrastructure.common.response.ServiceResponse;
import com.example.demo.starter.presentation.controller.base.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/meeting")
public class MeetingController extends BaseController {
    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @GetMapping
    @Operation(summary = "-TEST- Get All Meeting Services")
    public ResponseEntity<ServiceResponse<List<MeetingDto>>> get() {
        return controllerResponse(meetingService.get(0, 0));
    }

    @GetMapping("/{id}")
    @Operation(summary = "-TEST- Get Meeting By Id")
    public ResponseEntity<ServiceResponse<MeetingDto>> get(@PathVariable UUID id) {
        return controllerResponse(meetingService.getSingle(id));
    }

    @PostMapping
    @Operation(summary = "Create Meeting From Meeting")
    public ResponseEntity<ServiceResponse<MeetingDto>> create(@RequestPart MultipartFile file,
                                                              @RequestPart String title) throws IOException, InterruptedException {
        return controllerResponse(meetingService.upload(file, title));
    }

    @PostMapping("transcript")
    @Operation(summary = "Create Meeting From Transcript")
    public ResponseEntity<ServiceResponse<MeetingDto>> create(@RequestPart String transcript,
                                                              @RequestPart String title) throws IOException, InterruptedException {
        return controllerResponse(meetingService.upload(transcript, title));
    }

    @PutMapping
    @Operation(summary = "-TEST- Update Meeting")
    public ResponseEntity<ServiceResponse<MeetingDto>> update(MeetingDto model) {
        return controllerResponse(meetingService.update(model, model.getId()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "-TEST- Update Meeting")
    public ResponseEntity<ServiceResponse<NoContent>> update(@PathVariable UUID id) {
        return controllerResponse(meetingService.delete(id));
    }
}
