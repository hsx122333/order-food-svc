package com.mobai.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Test {

    private static String username;
    @Autowired
    private RedisUtil redisUtil;

    @Value("${test.user}")
    public void setUsername(String user) {
        username = user;
    }

    public static void sysUserName() {
        System.out.println("===========测试static静态注入：" + username);
    }
}
