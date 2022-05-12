package com.blue.limit.config;

import com.blue.limit.utils.AccessLimit;
import com.blue.limit.utils.IpUtil;
import com.blue.limit.utils.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AccessLimitInterceptor implements HandlerInterceptor {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //如果请求输入方法
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            //获取方法中的注解,看是否有该注解
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit != null) {
                long seconds = accessLimit.time();
                int maxCount = accessLimit.count();
                // 实现简单，使用 URL(172.29.112.1:8080/limit) 进行限流
                String key = IpUtil.getRealIp(request) + ":"+ request.getServerPort() + request.getContextPath() + request.getServletPath();
                //从redis中获取用户访问的次数
                try {
                    long count = redisService.incr(key, seconds);//此操作代表获取该key对应的值自增1后的结果
                    if (count > maxCount) {
                        logger.warn("请求过于频繁请稍后再试");
                        returnData(response);
                        return false;
                    }
                    return true;
                }catch (RedisConnectionFailureException e){
                    logger.info("redis错误"+e.getMessage().toString());
                    return true;
                }
            }
        }
        return false;
    }

    public void returnData(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        ObjectMapper objectMapper = new ObjectMapper();
        //这里传提示语可以改成自己项目的返回数据封装的类
        response.getWriter().println(objectMapper.writeValueAsString("请求过于频繁请稍后再试"));
        return;
    }
}
