package com.blue.limit.controller;


import com.blue.limit.utils.AccessLimit;
import com.blue.limit.utils.IpUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.plugin2.util.SystemUtil;

import javax.servlet.http.HttpServletRequest;

@RestController
public class LimitController {

    @GetMapping("/limit")
    @AccessLimit(key = "",time = 1,count = 1000) // 每秒处理 1000 个请求
    public String limit(HttpServletRequest request){
        return "正常处理请求！！";
    }
}
