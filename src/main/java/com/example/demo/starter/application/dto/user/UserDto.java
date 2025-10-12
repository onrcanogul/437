package com.example.demo.starter.application.dto.user;

import com.example.demo.starter.application.dto.base.BaseDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UserDto extends BaseDto {
    private String username;
    private String email;
    private List<String> roles = new ArrayList<>();
}
