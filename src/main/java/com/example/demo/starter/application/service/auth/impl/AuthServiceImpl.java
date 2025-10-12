package com.example.demo.starter.application.service.auth.impl;

import com.example.demo.starter.application.dto.user.LoginDto;
import com.example.demo.starter.application.dto.user.RegisterDto;
import com.example.demo.starter.domain.entity.RefreshToken;
import com.example.demo.starter.domain.entity.User;
import com.example.demo.starter.infrastructure.exception.BadRequestException;
import com.example.demo.starter.infrastructure.exception.NotFoundException;
import com.example.demo.starter.infrastructure.repository.RefreshTokenRepository;
import com.example.demo.starter.infrastructure.repository.UserRepository;
import com.example.demo.starter.application.service.auth.CustomUserDetailsService;
import com.example.demo.starter.application.service.auth.JwtService;
import com.example.demo.starter.application.service.auth.AuthService;
import com.example.demo.starter.infrastructure.common.response.AuthResponse;
import com.example.demo.starter.infrastructure.common.response.ServiceResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService customUserDetailsService;

    public AuthServiceImpl(AuthenticationManager authManager,
                           PasswordEncoder passwordEncoder,
                           UserRepository userRepository,
                           JwtService jwtService,
                           RefreshTokenRepository refreshTokenRepository,
                           CustomUserDetailsService customUserDetailsService
    ) {
        this.authManager = authManager;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.customUserDetailsService = customUserDetailsService;
    }

    public ServiceResponse<AuthResponse> login(LoginDto request) {
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails user = (UserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(user);
        RefreshToken refreshToken = setRefreshToken(user.getUsername());
        refreshTokenRepository.save(refreshToken);
        return ServiceResponse.success(new AuthResponse(token, refreshToken.getToken()), 200);
    }

    public ServiceResponse<AuthResponse> loginWithRefreshToken(String refreshToken){
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new NotFoundException("localize(notFound.refreshTokenNotFound)"));
        if (refreshTokenEntity.getExpiration().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("localize(badRequest.badRequestException)");
        }
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(refreshTokenEntity.getUser().getUsername());
        String accessToken = jwtService.generateToken(userDetails);
        refreshTokenEntity.setToken(UUID.randomUUID().toString());
        refreshTokenEntity.setExpiration(LocalDateTime.now().plusDays(7));
        RefreshToken updatedRefreshToken = refreshTokenRepository.save(refreshTokenEntity);
        return ServiceResponse.success(new AuthResponse(accessToken, updatedRefreshToken.getToken()), 200);
    }

    public ServiceResponse<String> register(RegisterDto request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BadRequestException("localize(badRequest.usernameAlreadyTaken)");
        }
        User user = new User();
        user.setRoles(List.of("USER"));
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return ServiceResponse.success("localize(Success)", 201);
    }


    private RefreshToken setRefreshToken(String username) {
        RefreshToken refreshTokenEntity;
        String refreshToken = UUID.randomUUID().toString();
        Optional<RefreshToken> userRefreshToken = refreshTokenRepository.findByUser_Username(username);
        if (userRefreshToken.isPresent()) {
            refreshTokenEntity = userRefreshToken.get();
        }
        else {
            refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setUser(userRepository.findByUsername(username).orElseThrow());
        }
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setExpiration(LocalDateTime.now().plusDays(7));
        return refreshTokenEntity;
    }
}
