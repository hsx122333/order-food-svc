package com.mobai.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final Logger log = LoggerFactory.getLogger(WebMvcConfig.class);
    private TokenInterceptor tokenInterceptor;

    //构造方法
    @Autowired
    public WebMvcConfig(TokenInterceptor tokenInterceptor) {
        this.tokenInterceptor = tokenInterceptor;
    }

    //解决跨域问题
    @Override
    public void addCorsMappings(CorsRegistry registry) {
      /*addMapping：允许所有映射
      allowedHeaders：允许所有请求头
      allowedMethods:允许所有请求方式，get、post、put、delete
      allowedOrigins：允许所有域名访问
      allowCredentials：允许携带cookie参数*/
        registry.addMapping("/**").allowedHeaders("*")
                .allowedMethods("*")
                .allowedOrigins("*")
                .allowCredentials(true);
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(new ConcurrentTaskExecutor(Executors.newFixedThreadPool(3)));
        configurer.setDefaultTimeout(30000);
    }

    /**
     * 配置拦截器，拦截未登陆请求
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> excludePath = new ArrayList<>();
        //排除拦截，除了注册登录(此时还没token)，其他都拦截
        excludePath.add("/static/**");//静态资源
        excludePath.add("/uploads/**");//静态资源，上传的文件
        excludePath.add("/staff/login");  //登录
        excludePath.add("/staff/insertStaff");  //注册
        excludePath.add("/task/**");  //定时任务接口
        excludePath.add("/staff/sendPassCode");  // 发送验证码
        excludePath.add("/staff/forgetPassword");  // 找回密码
        excludePath.add("/merchants/forgetPassword");//商家重置密码
        excludePath.add("/merchants/login");//商家登录
        excludePath.add("/swagger-ui.html");  // swagger-ui.html
        excludePath.add("/webjars/**");  // swagger静态资源
        excludePath.add("/swagger-resources/**"); // swagger静态资源
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(excludePath);
        WebMvcConfigurer.super.addInterceptors(registry);
    }

    /**
     * 配置静态文件在jar包同级目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            //获取jar所在位置绝对路径 jar/logistics/uploads/
            String gitPath = System.getProperty("user.dir") + File.separator + "logistics" + File.separator + "uploads" + File.separator;
            File file = new File(gitPath);
            if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
                if (!mkdirs) {
                    log.error("创建静态文件映射失败！");
                }
            }
            //这里是映射外部文件的代码:
            registry.addResourceHandler("/uploads/**").addResourceLocations("file:" + gitPath);
            //映射swagger
            registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/", "classpath:/META-INF/resources/webjars/");
            //这里是映射你项目的静态资源的:
            registry.addResourceHandler("/static").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/static/");
            WebMvcConfigurer.super.addResourceHandlers(registry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
