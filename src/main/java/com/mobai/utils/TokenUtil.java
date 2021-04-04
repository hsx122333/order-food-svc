package com.mobai.utils;


import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;

public class TokenUtil {
    private static Logger log = LoggerFactory.getLogger(TokenUtil.class);
    private static final long EXPIRE_TIME = 10 * 60 * 60 * 1000;
    private static final String TOKEN_SECRET = "ZhuXinRaoOrderFood";  //密钥盐

    /**
     * 签名生成
     *
     * @param info 有效信息
     * @return token
     */
    public static String sign(String info) {
        String token = null;
        try {
            Date expiresAt = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            token = JWT.create()
                    .withIssuer("auth0")
                    .withClaim("info", info)
                    .withExpiresAt(expiresAt)
                    // 使用了HMAC256加密算法。
                    .sign(Algorithm.HMAC256(TOKEN_SECRET));
        } catch (Exception e) {
            log.info("=============生成Token失败：" + e.getMessage());
            e.printStackTrace();
        }
        log.info("=============生成Token成功：" + token);
        return token;
    }

    /**
     * 签名验证
     *
     * @param token 请求携带的令牌
     * @return 是否通过校验
     */
    public static boolean verify(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("auth0").build();
            verifier.verify(token);
            log.info("=============验证通过：" + LocalDateTime.now());
            return true;
        } catch (Exception e) {
            log.info("=============Token解析失败：" + e.getMessage());
            return false;
        }
    }


    /**
     * 有效负载解析
     *
     * @param token 令牌
     * @return StaffDTO
     */
    private static JSONObject getStaffInfo(String token) {
        String[] split = token.split("\\.");
        String staffStr = StringUtils.newStringUtf8(Base64.decodeBase64(split[1]));
        JSONObject jsonObject = JSONObject.parseObject(staffStr);
        return jsonObject.getJSONObject("info");
    }

    /**
     * 有效负载解析
     *
     * @param request 请求
     * @return StaffDTO
     */
    public static JSONObject getStaffInfo(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (org.springframework.util.StringUtils.isEmpty(token)) {
            throw new RuntimeException("token不正确！");
        }
        return getStaffInfo(token);
    }


    /*public static void main(String[] args) {
     *//*StaffDTO staffDTO = new StaffDTO();
        staffDTO.setStaffCode("admin");
        staffDTO.setPhone("15320279011");
        staffDTO.setStaffName("超级管理员！");
        String sign = TokenUtil.sign(staffDTO);
        System.out.println(sign);*//*
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoMCIsImV4cCI6MTYxNTU1NjY1OSwic3RhZmZJbmZvIjoie1wicGhvbmVcIjpcIjE1MzIwMjc5MDExXCIsXCJzdGFmZkNvZGVcIjpcImFkbWluXCIsXCJzdGFmZk5hbWVcIjpcIui2hee6p-euoeeQhuWRmO-8gVwifSJ9.OPVNwjYBpPagFbmxzzuX7A_hD5Wykmit2caXcamPscc";
        boolean bool = TokenUtil.verify(token);
        if (bool) {
            StaffDTO staffInfo = TokenUtil.getStaffInfo(token);
            System.out.println(staffInfo);
        }
    }*/

}
