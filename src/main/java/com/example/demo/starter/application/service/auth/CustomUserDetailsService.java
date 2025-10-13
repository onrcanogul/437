package com.example.demo.starter.application.service.auth;

import com.example.demo.starter.application.dto.user.UserDto;
import com.example.demo.starter.infrastructure.util.response.ServiceResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.UUID;

public interface CustomUserDetailsService extends UserDetailsService {
    ServiceResponse<List<UserDto>> search(String term);
    UUID getCurrentUserId();
    UUID getCurrentTeamId();
}
