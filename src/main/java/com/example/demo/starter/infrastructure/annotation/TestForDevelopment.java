package com.example.demo.starter.infrastructure.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TestForDevelopment {
    String value() default "Development only";
}
