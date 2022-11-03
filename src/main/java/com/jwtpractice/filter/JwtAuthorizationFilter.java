package com.jwtpractice.filter;

import com.jwtpractice.domain.user.User;
import com.jwtpractice.domain.user.UserRepository;
import com.jwtpractice.jwt.JwtProperties;
import com.jwtpractice.jwt.JwtTokenProvider;
import com.jwtpractice.jwt.SessionJwtTokenProvider;
import com.jwtpractice.service.PrincipalDetails;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;
    private JwtTokenProvider jwtTokenProvider;
    private SessionJwtTokenProvider sessionJwtTokenProvider;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, JwtTokenProvider jwtTokenProvider, SessionJwtTokenProvider sessionJwtTokenProvider) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.sessionJwtTokenProvider = sessionJwtTokenProvider;

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>> 인증에 필요한 페이지 접근 <<<<<<<<<<<<<<<<<<<<<<<<<<<");

        PrintWriter out = response.getWriter();
        String accessToken = null;
        String refreshToken = null;
        String userId = null;

        String requestUrI = request.getRequestURI();
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>> {} <<<<<<<<<<<<<<<<<<<<<<<<<<<", requestUrI);
        if(requestUrI.equals("/response")) {
            return;
        }
        if(requestUrI.equals("/main") || requestUrI.equals("/favicon.ico")) {
            chain.doFilter(request, response);
            return;
        }else if(requestUrI.equals("/user") || requestUrI.equals("/admin")) {
            accessToken = sessionJwtTokenProvider.loadAccessTokenInSession(request.getSession());
            refreshToken = sessionJwtTokenProvider.loadRefreshTokenInSession(request.getSession());

        }else {
            accessToken = request.getHeader(JwtProperties.ACCESS_TOKEN);
            refreshToken = request.getHeader(JwtProperties.REFRESH_TOKEN);

            accessToken = accessToken.equals("null") ? null : accessToken;
            refreshToken = refreshToken.equals("null") ? null : refreshToken;

        }
//        super.doFilterInternal(request, response, chain);

        if(accessToken == null || refreshToken == null) {
            out.print(sendForbiddenError(response));
            return;
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
                return;
            }
        }

        userId = userId == null ? (String) jwtTokenProvider.getClaimsByAccessToken(accessToken).get("userId") : userId;

        User user = userRepository.findUserByUserId(userId);
        PrincipalDetails principalDetails = new PrincipalDetails(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>> 권한 등록 <<<<<<<<<<<<<<<<<<<<<<<<<<<");
        chain.doFilter(request, response);

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