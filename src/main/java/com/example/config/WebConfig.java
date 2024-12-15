package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.CacheControl;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${app.upload.avatar-dir}")
    private String avatarUploadDir;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**")
               .addResourceLocations("classpath:/static/css/")
               .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS));
        registry.addResourceHandler("/js/**")
               .addResourceLocations("classpath:/static/js/")
               .setCacheControl(CacheControl.noCache());
        registry.addResourceHandler("/images/**")
               .addResourceLocations("classpath:/static/images/")
               .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS));
        registry.addResourceHandler("/files/**")
               .addResourceLocations("file:" + avatarUploadDir + "/");
    }
} 