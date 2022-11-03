package com.jwtpractice.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwtpractice.domain.user.User;
import com.jwtpractice.jwt.JwtProperties;
import com.jwtpractice.jwt.JwtTokenProvider;
import com.jwtpractice.jwt.SessionJwtTokenProvider;
import com.jwtpractice.service.PrincipalDetails;
import com.jwtpractice.web.dto.CustomResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SessionJwtTokenProvider sessionJwtTokenProvider;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        User user = null;

        ObjectMapper mapper = new ObjectMapper();
        try {
            user = mapper.readValue(request.getInputStream(), User.class);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUser_id(), user.getUser_password());

            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println("로그인 성공: " + principalDetails.getUser());

            return authentication;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("인증이 완료됨");
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        String accessToken = jwtTokenProvider.createAccessToken(principalDetails.getUsername());
        String refreshToken = jwtTokenProvider.createRefreshToken(principalDetails.getUsername());

        response.addHeader(JwtProperties.ACCESS_TOKEN, JwtProperties.TOKEN_PREFIX + accessToken);
        response.addHeader(JwtProperties.REFRESH_TOKEN, JwtProperties.TOKEN_PREFIX + refreshToken);

        sessionJwtTokenProvider.saveAccessTokenInSession(request.getSession(), accessToken);
        sessionJwtTokenProvider.saveRefreshTokenInSession(request.getSession(), refreshToken);
    }

}