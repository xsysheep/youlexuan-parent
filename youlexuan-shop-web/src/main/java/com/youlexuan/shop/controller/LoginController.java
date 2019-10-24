package com.youlexuan.shop.controller;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    /**
     * 显示登录用户
     * @return
     */
    @RequestMapping("loginName")
    public Map loginName(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //map初始16 75%的自增,算法 需要大于需求/0.75
        Map map = new HashMap(4);
        map.put("loginName",name);
        map.put("lastLoginTime",new Date());
        return map;
    }
}
