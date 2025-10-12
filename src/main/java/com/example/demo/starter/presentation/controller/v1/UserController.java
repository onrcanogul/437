package com.example.demo.starter.presentation.controller.v1;

import com.example.demo.starter.application.dto.user.UserDto;
import com.example.demo.starter.application.service.auth.CustomUserDetailsService;
import com.example.demo.starter.infrastructure.common.response.ServiceResponse;
import com.example.demo.starter.presentation.controller.base.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController extends BaseController {
    private final CustomUserDetailsService userService;

    public UserController(CustomUserDetailsService userService) {
        this.userService = userService;
    }

    @GetMapping("{term}")
    public ResponseEntity<ServiceResponse<List<UserDto>>> search(@PathVariable String term) {
        return controllerResponse(userService.search(term));
    }
}
