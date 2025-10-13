package com.example.demo.starter.presentation.controller.base;

import com.example.demo.starter.infrastructure.util.response.ServiceResponse;
import org.springframework.http.ResponseEntity;

public class BaseController {
    protected <T> ResponseEntity<ServiceResponse<T>> controllerResponse(ServiceResponse<T> response) {
        return ResponseEntity
                .status(response.getStatusCode())
                .body(response);
    }
}
