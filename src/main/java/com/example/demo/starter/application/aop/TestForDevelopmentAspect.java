package com.example.demo.starter.application.aop;

import com.example.demo.starter.infrastructure.annotation.TestForDevelopment;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class TestForDevelopmentAspect {
    @Value("${spring.profiles.active:}")
    private String activeProfile;

    @Around(value = "@annotation(testForDevelopment)")
    public Object checkProfile(ProceedingJoinPoint joinPoint, TestForDevelopment testForDevelopment) throws Throwable {
        if (!"dev".equalsIgnoreCase(activeProfile)) {
            System.out.println("Skipping dev-only method: " + joinPoint.getSignature().getName());
            return null;
        }
        return joinPoint.proceed();
    }
}
