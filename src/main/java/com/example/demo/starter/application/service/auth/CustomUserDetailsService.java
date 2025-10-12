package com.example.demo.starter.application.service.auth;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

public interface CustomUserDetailsService extends UserDetailsService {
    UUID getCurrentUserId();
}
