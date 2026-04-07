package com.example.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // http://localhost:8080/images/ファイル名 でアクセスできるようにする
        // file: をつけることで、実行環境の絶対パスまたは相対パスとしてフォルダを指定します
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:user-photos/");
    }
}