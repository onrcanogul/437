package com.example.demo.starter.presentation.controller.v1;

import com.example.demo.starter.application.dto.pbi.ProductBacklogItemDto;
import com.example.demo.starter.application.service.pbi.ProductBacklogItemService;
import com.example.demo.starter.infrastructure.common.response.NoContent;
import com.example.demo.starter.infrastructure.common.response.ServiceResponse;
import com.example.demo.starter.presentation.controller.base.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/product-backlog-item")
public class ProductBacklogItemController extends BaseController {
    private final ProductBacklogItemService productBacklogItemService;

    public ProductBacklogItemController(ProductBacklogItemService productBacklogItemService) {
        this.productBacklogItemService = productBacklogItemService;
    }

    @GetMapping
    @Operation(summary = "-TEST- Get All Product Backlog Items")
    public ResponseEntity<ServiceResponse<List<ProductBacklogItemDto>>> get() {
        return controllerResponse(productBacklogItemService.get(0, 0));
    }

    @GetMapping("/meeting/{meetingId}")
    @Operation(summary = "-TEST- Get Product Backlog Item By Id")
    public ResponseEntity<ServiceResponse<List<ProductBacklogItemDto>>> getByMeeting(@PathVariable UUID meetingId) {
        return controllerResponse(productBacklogItemService.getByMeeting(meetingId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Product Backlog Item By Id")
    public ResponseEntity<ServiceResponse<ProductBacklogItemDto>> get(@PathVariable UUID id) {
        return controllerResponse(productBacklogItemService.getSingle(id));
    }

    @PostMapping
    @Operation(summary = "-TEST- Create PBI")
    public ResponseEntity<ServiceResponse<ProductBacklogItemDto>> create(ProductBacklogItemDto model) {
        return controllerResponse(productBacklogItemService.create(model));
    }

    @PutMapping
    @Operation(summary = "-TEST- Update PBI")
    public ResponseEntity<ServiceResponse<ProductBacklogItemDto>> update(ProductBacklogItemDto model) {
        return controllerResponse(productBacklogItemService.update(model, model.getId()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "-TEST- Delete PBI")
    public ResponseEntity<ServiceResponse<NoContent>> update(@PathVariable UUID id) {
        return controllerResponse(productBacklogItemService.delete(id));
    }
}
