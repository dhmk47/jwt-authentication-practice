package com.jwtpractice.filter;

import com.jwtpractice.domain.user.User;
import com.jwtpractice.domain.user.UserRepository;
import com.jwtpractice.jwt.JwtProperties;
import com.jwtpractice.jwt.JwtTokenProvider;
import com.jwtpractice.jwt.SessionJwtTokenProvider;
import com.jwtpractice.service.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenAuthorizationFilter implements Filter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final SessionJwtTokenProvider sessionJwtTokenProvider;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        log.info(">>>>>>>>>>>>>>>>>> 필터");
        String uri = request.getRequestURI();
        log.info(">>>>>>>>>>>>>>>>>> {}", uri);
        String accessToken = null;
        String refreshToken = null;
        String userId = null;
        String role = null;
        String requestHeader = request.getHeader("X-Requested-With");

        if(uri.contains("/login") || uri.contains("/join")) {
            chain.doFilter(request, response);
            return;
        }

        accessToken = sessionJwtTokenProvider.loadAccessTokenInSession(request.getSession());
        refreshToken = sessionJwtTokenProvider.loadRefreshTokenInSession(request.getSession());

        log.info(">>>>>>>>>>>>>>>> accessToken: {}", accessToken);
        log.info(">>>>>>>>>>>>>>>> refreshToken: {}", refreshToken);

        if(accessToken == null || refreshToken == null) {
            log.info(">>>>>>>>>>>>>>>>>> null!");
            sendForbiddenError(response, requestHeader);
            return;
        }

        if(uri.equals("/user") || uri.equals("/admin")) {
            role = uri.equals("/user") ? "ROLE_USER" : "ROLE_ADMIN";

        }else if(uri.contains("/auth")) {
//            accessToken = request.getHeader(JwtProperties.ACCESS_TOKEN);
//            refreshToken = request.getHeader(JwtProperties.REFRESH_TOKEN);

//            accessToken = accessToken.equals("null") ? null : accessToken;
//            refreshToken = refreshToken.equals("null") ? null : refreshToken;

            role = "ROLE_USER";

            log.info(">>>>>>>>>>>>>>>>>> 여기");

        }else {
            chain.doFilter(request, response);
            return;
        }

        if(!jwtTokenProvider.isValidAccessToken(accessToken)) {

            if(jwtTokenProvider.isValidRefreshToken(refreshToken)) {

                userId = (String) jwtTokenProvider.getClaimsByRefreshToken(refreshToken).get("userId");

                accessToken = jwtTokenProvider.createAccessToken(userId);
                refreshToken = jwtTokenProvider.createRefreshToken(userId);

                log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>> 새로 발급 <<<<<<<<<<<<<<<<<<<<<<<<<<<");

//                response.setHeader(JwtProperties.ACCESS_TOKEN, accessToken);
//                response.setHeader(JwtProperties.REFRESH_TOKEN, refreshToken);

                sessionJwtTokenProvider.saveAccessTokenInSession(request.getSession(), accessToken);
                sessionJwtTokenProvider.saveRefreshTokenInSession(request.getSession(), refreshToken);

                userId = (String) jwtTokenProvider.getClaimsByAccessToken(accessToken).get("userId");
            }else {
                sendForbiddenError(response, requestHeader);
                return;
            }
        }

        userId = userId == null ? (String) jwtTokenProvider.getClaimsByAccessToken(accessToken).get("userId") : userId;

        User user = userRepository.findUserByUserId(userId);

        if(!hasRole(user, role)) {
            sendForbiddenError(response, requestHeader);
            return;
        }

//        PrincipalDetails principalDetails = new PrincipalDetails(user);
//
//        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>> 권한 체크 완료 <<<<<<<<<<<<<<<<<<<<<<<<<<<");
        chain.doFilter(request, response);

    }

    private void sendForbiddenError(HttpServletResponse response, String requestHeader) throws IOException {
        if(requestHeader == null) {
            response.getWriter().print(getForbiddenErrorMessage(response));

        }else if(isAjaxRequest(requestHeader)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);

        }
    }

    private boolean isAjaxRequest(String requestHeader) {
        return requestHeader.equals("XMLHttpRequest");
    }

    private String getForbiddenErrorMessage(HttpServletResponse response) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        stringBuilder.append("<html><body><script>");
        stringBuilder.append("alert(\'접근 권한이 없습니다.\');");
        stringBuilder.append("location.replace(\'/main\');");
        stringBuilder.append("</script></body></html>");

        return stringBuilder.toString();
    }

    private boolean hasRole(User user, String role) {
        boolean status = false;

        if(user.getUserRoles().contains(role)) {
            status = true;
        }

        return status;
    }
}
