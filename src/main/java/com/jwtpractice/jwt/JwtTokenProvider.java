package com.jwtpractice.jwt;

import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
@Component
@Getter
public class JwtTokenProvider {

    private String secretKey = "secretKey";
    private String refreshKey = "refreshKey";

    private final long ACCESS_TOKEN_VALID_TIME = 1 * 60 * 1000L;        // 1분
    private final long REFRESH_TOKEN_VALID_TIME = 60 * 60 * 24 * 7 * 1000L; // 7일

    // @PostConstruct는 의존성 주입이 이루어진 후 초기화를 수행하는 메서드
    @PostConstruct
    protected void init() {     // secretKey를 Base64로 인코딩
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        refreshKey = Base64.getEncoder().encodeToString(refreshKey.getBytes());
    }

    public String createAccessToken(String userId) {
        Date date = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("daegyeong")
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_VALID_TIME))
                .claim("userId", userId)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createRefreshToken(String userId) {
        Date date = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("daegyeong")
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_VALID_TIME))
                .claim("userId", userId)
                .signWith(SignatureAlgorithm.HS256, refreshKey)
                .compact();
    }

    public String resolveAccessToken(HttpServletRequest request) {
        return request.getHeader(JwtProperties.ACCESS_TOKEN);
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        return request.getHeader(JwtProperties.REFRESH_TOKEN);
    }

    public Claims getClaimsByAccessToken(String token) {
        System.out.println("getAccess: " + token);
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token.replaceAll(JwtProperties.TOKEN_PREFIX, ""))
                .getBody();
    }

    public Claims getClaimsByRefreshToken(String token) {
        System.out.println("getRefresh: " + token);
        return Jwts.parser()
                .setSigningKey(refreshKey)
                .parseClaimsJws(token.replaceAll(JwtProperties.TOKEN_PREFIX, ""))
                .getBody();
    }

    public boolean isValidAccessToken(String token) {
        try {
            Claims accessClaims = getClaimsByAccessToken(token);
            System.out.println("Access expireTime: " + accessClaims.getExpiration());
            System.out.println("Access userId: " + accessClaims.get("userId"));
            return true;
        } catch (ExpiredJwtException exception) {
            System.out.println("Token Expired UserID : " + exception.getClaims().get("userId"));
            return false;
        } catch (JwtException exception) {
            exception.printStackTrace();
            System.out.println("Token Tampered");
            return false;
        } catch (NullPointerException exception) {
            System.out.println("Token is null");
            return false;
        }
    }

    public boolean isValidRefreshToken(String token) {
        try {
            Claims refreshClaims = getClaimsByRefreshToken(token);
            System.out.println("Access expireTime: " + refreshClaims.getExpiration());
            System.out.println("Access userId: " + refreshClaims.get("userId"));
            return true;
        } catch (ExpiredJwtException exception) {
            System.out.println("Token Expired UserID : " + exception.getClaims().get("userId"));
            return false;
        } catch (JwtException exception) {
            System.out.println("Token Tampered");
            return false;
        } catch (NullPointerException exception) {
            System.out.println("Token is null");
            return false;
        }
    }

    public boolean isOnlyExpiredToken(String token) {
        try {
            Claims accessClaims = getClaimsByAccessToken(token);
            System.out.println("Access expireTime: " + accessClaims.getExpiration());
            System.out.println("Access userId: " + accessClaims.get("userId"));
            return false;
        } catch (ExpiredJwtException exception) {
            System.out.println("Token Expired UserID : " + exception.getClaims().get("userId"));
            return true;
        } catch (JwtException exception) {
            System.out.println("Token Tampered");
            return false;
        } catch (NullPointerException exception) {
            System.out.println("Token is null");
            return false;
        }
    }
}