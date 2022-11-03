package com.jwtpractice.service.auth;

import com.jwtpractice.web.dto.TokenResponse;
import com.jwtpractice.web.dto.user.SignInUserRequestDto;
import com.jwtpractice.web.dto.user.SignupUserRequestDto;

public interface AuthService {
    public boolean insertUser(SignupUserRequestDto signupUserRequestDto) throws Exception;
    public TokenResponse signInUser(SignInUserRequestDto signInUserRequestDto) throws Exception;
}