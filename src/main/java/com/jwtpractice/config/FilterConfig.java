package com.jwtpractice.config;

import com.jwtpractice.filter.TokenAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final TokenAuthorizationFilter tokenAuthorizationFilter;

    @Bean
    public FilterRegistrationBean<TokenAuthorizationFilter> tokenAuthorizationFilterFilterRegistrationBean() {
        FilterRegistrationBean<TokenAuthorizationFilter> bean = new FilterRegistrationBean<TokenAuthorizationFilter>(tokenAuthorizationFilter);
        bean.setOrder(0);
        bean.addUrlPatterns("/api/v1/auth/*", "/user", "/admin");
        return bean;
    }
}
