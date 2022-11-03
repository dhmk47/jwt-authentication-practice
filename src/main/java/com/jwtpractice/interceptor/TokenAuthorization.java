package com.jwtpractice.interceptor;

import com.jwtpractice.domain.user.User;
import com.jwtpractice.domain.user.UserRepository;
import com.jwtpractice.jwt.JwtProperties;
import com.jwtpractice.jwt.JwtTokenProvider;
import com.jwtpractice.jwt.SessionJwtTokenProvider;
import com.jwtpractice.service.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthorization implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final SessionJwtTokenProvider sessionJwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info(">>>>>>>>>>>>>>>>>> 인터셉터");
        String uri = request.getRequestURI();
        String accessToken = null;
        String refreshToken = null;
        String userId = null;
        PrintWriter out = response.getWriter();

        if(uri.equals("/user") || uri.equals("/admin")) {
            accessToken = sessionJwtTokenProvider.loadAccessTokenInSession(request.getSession());
            refreshToken = sessionJwtTokenProvider.loadRefreshTokenInSession(request.getSession());

        }else {
            accessToken = request.getHeader(JwtProperties.ACCESS_TOKEN);
            refreshToken = request.getHeader(JwtProperties.REFRESH_TOKEN);

            accessToken = accessToken.equals("null") ? null : accessToken;
            refreshToken = refreshToken.equals("null") ? null : refreshToken;

        }

        if(accessToken == null && refreshToken == null) {
            out.print(sendForbiddenError(response));
            return false;
        }

        if(!jwtTokenProvider.isValidAccessToken(accessToken)) {

            if(jwtTokenProvider.isValidRefreshToken(refreshToken)) {

                userId = (String) jwtTokenProvider.getClaimsByRefreshToken(refreshToken).get("userId");

                accessToken = jwtTokenProvider.createAccessToken(userId);
                refreshToken = jwtTokenProvider.createRefreshToken(userId);

                log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>> 새로 발급 <<<<<<<<<<<<<<<<<<<<<<<<<<<");

                response.setHeader(JwtProperties.ACCESS_TOKEN, accessToken);
                response.setHeader(JwtProperties.REFRESH_TOKEN, refreshToken);

                sessionJwtTokenProvider.saveAccessTokenInSession(request.getSession(), accessToken);
                sessionJwtTokenProvider.saveRefreshTokenInSession(request.getSession(), refreshToken);

                userId = (String) jwtTokenProvider.getClaimsByAccessToken(accessToken).get("userId");
            }else {
                out.print(sendForbiddenError(response));
                return false;
            }
        }

        userId = userId == null ? (String) jwtTokenProvider.getClaimsByAccessToken(accessToken).get("userId") : userId;

        User user = userRepository.findUserByUserId(userId);
        PrincipalDetails principalDetails = new PrincipalDetails(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>> 권한 등록 <<<<<<<<<<<<<<<<<<<<<<<<<<<");

        return true;
    }

    private String sendForbiddenError(HttpServletResponse response) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        stringBuilder.append("<html><body><script>");
        stringBuilder.append("alert(\'접근 권한이 없습니다.\');");
        stringBuilder.append("location.replace(\'/main\');");
        stringBuilder.append("</script></body></html>");

        return stringBuilder.toString();
    }
}
