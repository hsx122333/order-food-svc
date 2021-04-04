package com.mobai.utils;


import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Random;

/**
 * Created on 17/6/7.
 * 短信API产品的DEMO程序,工程中包含了一个SmsDemo类，直接通过
 * 执行main函数即可体验短信产品API功能(只需要将AK替换成开通了云通信-短信产品功能的AK即可)
 * 工程依赖了2个jar包(存放在工程的libs目录下)
 * 1:aliyun-java-sdk-core.jar
 * 2:aliyun-java-sdk-dysmsapi.jar
 * <p>
 * 备注:Demo工程编码采用UTF-8
 * 国际短信发送请勿参照此DEMO
 */
@Component
public class SmsUtil {
    //申请的短信产品ID
    private static String accessKeyId;
    //申请的产品密钥
    private static String accessSecret;
    //电话号码
//    private static final String phoneNum = "13340268882";
    //签名
    private static String signName;
    //模板
    private static String templateCode;

    private static final boolean isTest = true;

    @Value("${accessKey_id}")
    public static void setAccessKeyId(String accessKey_id) {
        accessKeyId = accessKey_id;
    }
    @Value("${access_secret}")
    public static void setAccessSecret(String access_secret) {
        accessSecret = access_secret;
    }
    @Value("${sign_name}")
    public static void setSignName(String sign_name) {
        signName = sign_name;
    }
    @Value("${template_code}")
    public static void setTemplateCode(String template_code) {
        templateCode = template_code;
    }

    public static boolean sendMessage(String phoneNum, String code) {
        if (isTest){
            return true;
        }
        //连接阿里云
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);
        //构建请求
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");//不要动
        request.setVersion("2017-05-25");//不要动
        request.setAction("SendSms");
        //自定义参数（手机号，验证码，签名，模板）
        request.putQueryParameter("PhoneNumbers", phoneNum);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        //构建一个短信验证码
        HashMap<String, Object> map = new HashMap<>();
        map.put("code", code);
        String jsonString = JSONObject.toJSONString(map);
        request.putQueryParameter("TemplateParam", jsonString);
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 生成随机验证码
     *
     * @return 验证码
     */
    public static String randomCode() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int r = random.nextInt(10);
            code.append(r);
        }
        return code.toString();
    }

    public static void main(String[] args) {
        String code = randomCode();
        sendMessage("15320279012", code);
    }
}
