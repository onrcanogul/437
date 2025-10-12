package com.example.demo.starter.application.dto.user;

import java.util.List;
import java.util.UUID;

public record CurrentUser(UUID id, String username, List<String> roles) implements java.io.Serializable {}
