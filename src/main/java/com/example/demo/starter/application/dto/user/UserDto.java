package com.example.demo.starter.application.dto.user;

import com.example.demo.starter.application.dto.base.BaseDto;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UserDto extends BaseDto {
    private String username;
    private String email;
}
