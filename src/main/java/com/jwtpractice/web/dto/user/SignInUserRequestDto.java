package com.jwtpractice.web.dto.user;

import com.jwtpractice.domain.user.User;
import lombok.Data;

@Data
public class SignInUserRequestDto {
    private String user_id;
    private String user_password;
}