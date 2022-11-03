package com.jwtpractice.config;

import com.jwtpractice.domain.user.UserRepository;
import com.jwtpractice.jwt.JwtTokenProvider;
import com.jwtpractice.jwt.SessionJwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final SessionJwtTokenProvider sessionJwtTokenProvider;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        super.configure(http);
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //세션을 쓰지 않겠다.
                .and()
                .formLogin().disable() // form태그로 로그인하지 않겠다.
                .httpBasic().disable();

//                .addFilter(new JwtAuthenticationFilter(authenticationManager(), jwtTokenProvider, bCryptPasswordEncoder(), sessionJwtTokenProvider)) //AuthenticationManager파라미터 필요
//                .addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository, jwtTokenProvider, sessionJwtTokenProvider))
//                .authorizeRequests()
//                .antMatchers("/user/**", "/auth/**")
//                .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
//                .antMatchers("/admin/**")
//                .access("hasRole('ROLE_ADMIN')")
//                .anyRequest().permitAll();

    }
}