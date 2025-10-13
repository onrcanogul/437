package com.example.demo.starter.application.service.auth.impl;

import com.example.demo.starter.application.dto.user.CurrentUser;
import com.example.demo.starter.application.dto.user.UserDto;
import com.example.demo.starter.domain.entity.User;
import com.example.demo.starter.infrastructure.util.response.ServiceResponse;
import com.example.demo.starter.infrastructure.configuration.mapper.Mapper;
import com.example.demo.starter.infrastructure.exception.NotFoundException;
import com.example.demo.starter.infrastructure.repository.UserRepository;
import com.example.demo.starter.application.service.auth.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {
    private final UserRepository userRepository;
    private final Mapper<User, UserDto> mapper;

    @Autowired
    public CustomUserDetailsServiceImpl(UserRepository userRepository, Mapper<User, UserDto> mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public ServiceResponse<List<UserDto>> search(String term) {
        List<User> users = userRepository.findByUsernameContainingIgnoreCase(term);
        return ServiceResponse.success(users.stream().map(mapper::toDto).toList(), 200);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws NotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username: " + username));

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    @Override
    public UUID getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CurrentUser cu) return cu.id();
        return null;
    }

    @Override
    public UUID getCurrentTeamId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return null;

        HttpServletRequest request = attributes.getRequest();
        String teamIdHeader = request.getHeader("Team-Id");

        if (teamIdHeader == null || teamIdHeader.isBlank()) return null;

        try {
            return UUID.fromString(teamIdHeader);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Team-Id header format");
        }
    }
}
