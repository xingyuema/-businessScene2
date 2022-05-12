package com.blue.limit.utils;

import java.lang.annotation.*;

/**
* 限流注解
*/
@Inherited
@Documented
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {
    //限流唯一标识
    String key() default "127.0.0.1";

    //限流时间 单位为 s
    int time();

    //限流次数
    int count();
}

