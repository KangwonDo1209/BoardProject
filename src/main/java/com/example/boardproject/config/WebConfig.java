package com.example.boardproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private String resourcePath = "/upload/**"; // view 에서 접근할 경로
    @Value("${file.spring_img}")
    private String savePath;
    //private String savePath = "file:/" + System.getProperty("user.home") + "/Desktop/springboot_img/"; // 실제 파일 저장 경로
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        String path = "file:/" + savePath;
        registry.addResourceHandler(resourcePath)
                .addResourceLocations(path);
    }
}
