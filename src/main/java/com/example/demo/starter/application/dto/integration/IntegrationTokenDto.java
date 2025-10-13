package com.example.demo.starter.application.dto.integration;

import com.example.demo.starter.application.dto.base.BaseDto;
import com.example.demo.starter.application.dto.user.UserDto;
import com.example.demo.starter.domain.enumeration.ProviderType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class IntegrationTokenDto extends BaseDto {
    private ProviderType provider;
    private String token; // encrypted
    private String meta; // project name etc.
    private UserDto user;
    private String usernameAtProvider;
    private String emailAtProvider;
}
