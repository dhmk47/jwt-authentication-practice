package com.jwtpractice.web.controller.api;

import com.jwtpractice.jwt.JwtProperties;
import com.jwtpractice.jwt.SessionJwtTokenProvider;
import com.jwtpractice.service.auth.AuthService;
import com.jwtpractice.web.dto.CustomResponseDto;
import com.jwtpractice.web.dto.TokenResponse;
import com.jwtpractice.web.dto.user.SignInUserRequestDto;
import com.jwtpractice.web.dto.user.SignupUserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final SessionJwtTokenProvider sessionJwtTokenProvider;

    @PostMapping("/join")
    public ResponseEntity<?> signupUser(@RequestBody SignupUserRequestDto signupUserRequestDto) {
        boolean status = false;

        try {
            status = authService.insertUser(signupUserRequestDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new CustomResponseDto<>(1, "signup failed", status));
        }

        return ResponseEntity.ok().body(new CustomResponseDto<>(1, "signup successfully", status));
    }

    @PostMapping("/login")
    public ResponseEntity<?> signInUser(@RequestBody SignInUserRequestDto signInUserRequestDto, HttpServletRequest request, HttpServletResponse response) {
        TokenResponse tokenResponse = null;
        String accessToken = null;
        String refreshToken = null;

        try {
            tokenResponse = authService.signInUser(signInUserRequestDto);

            accessToken = tokenResponse.getAccessToken();
            refreshToken = tokenResponse.getRefreshToken();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new CustomResponseDto<>(1, "signup failed", false));
        }

//        response.setHeader(JwtProperties.ACCESS_TOKEN, JwtProperties.TOKEN_PREFIX + accessToken);
//        response.setHeader(JwtProperties.REFRESH_TOKEN, JwtProperties.TOKEN_PREFIX + refreshToken);
        sessionJwtTokenProvider.saveAccessTokenInSession(request.getSession(), accessToken);
        sessionJwtTokenProvider.saveRefreshTokenInSession(request.getSession(), refreshToken);


        return ResponseEntity.ok().body(new CustomResponseDto<>(1, "signup successfully", true));
    }

    @PutMapping("/test")
    public ResponseEntity<?> testUpdate(Map<String, Object> testMap) {
        boolean status = true;

        return ResponseEntity.ok().body(new CustomResponseDto<>(1, "signup successfully", status));
    }

}