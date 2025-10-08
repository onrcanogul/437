package com.example.demo.starter.application.service.auth;

import com.example.demo.starter.application.dto.user.LoginDto;
import com.example.demo.starter.application.dto.user.RegisterDto;
import com.example.demo.starter.infrastructure.common.response.AuthResponse;
import com.example.demo.starter.infrastructure.common.response.ServiceResponse;

public interface AuthService {
    ServiceResponse<AuthResponse> login(LoginDto request);
    ServiceResponse<AuthResponse> loginWithRefreshToken(String refreshToken);
    ServiceResponse<String> register(RegisterDto request);
}
