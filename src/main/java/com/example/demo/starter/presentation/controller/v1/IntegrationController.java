package com.example.demo.starter.presentation.controller.v1;

import com.example.demo.starter.application.service.integration.token.IntegrationService;
import com.example.demo.starter.domain.enumeration.ProviderType;
import com.example.demo.starter.infrastructure.util.response.NoContent;
import com.example.demo.starter.infrastructure.util.response.ServiceResponse;
import com.example.demo.starter.presentation.controller.base.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/integration")
public class IntegrationController extends BaseController {
    private final IntegrationService service;

    public IntegrationController(IntegrationService service) {
        this.service = service;
    }

    @PostMapping("/connect")
    public ResponseEntity<ServiceResponse<NoContent>> connect(
            @RequestParam ProviderType provider,
            @RequestParam String token,
            @RequestParam(required = false) String meta) {
        return controllerResponse(service.connectUser(provider, token, meta));
    }
}
