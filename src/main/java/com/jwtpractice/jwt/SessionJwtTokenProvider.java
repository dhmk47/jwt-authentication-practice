package com.jwtpractice.jwt;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Component
public class SessionJwtTokenProvider {

    public void saveAccessTokenInSession(HttpSession session, String accessToken) {
        session.setAttribute(JwtProperties.ACCESS_TOKEN, JwtProperties.TOKEN_PREFIX + accessToken);
    }

    public void saveRefreshTokenInSession(HttpSession session, String refreshToken) {
        session.setAttribute(JwtProperties.REFRESH_TOKEN, JwtProperties.TOKEN_PREFIX + refreshToken);
    }

    public String loadAccessTokenInSession(HttpSession session) {
        return (String) session.getAttribute(JwtProperties.ACCESS_TOKEN);
    }

    public String loadRefreshTokenInSession(HttpSession session) {
        return (String) session.getAttribute(JwtProperties.REFRESH_TOKEN);
    }
}