package com.jwtpractice.handler.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Aspect
@Component
@Slf4j
public class AuthorizationAop {

    @Pointcut("@annotation(com.jwtpractice.handler.aop.annotation.Authorization)")
    private void authorizate() {};

    @Before(value = "authorizate()")
    public void authorizationCheck(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        log.info(">>>>>>>>>>>>>>>>>> {}", request.getRequestURL());
//        String url = request.getRequestURI();
//
//        log.info(">>>>>>>>>!!!!!!!!!!!!! {}", url);
    }
}