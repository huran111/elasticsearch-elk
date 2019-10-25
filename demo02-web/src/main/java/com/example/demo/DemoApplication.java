package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import rest.RestServerInterceptor;

@SpringBootApplication
public class DemoApplication extends WebMvcConfigurerAdapter {
    private static RestServerInterceptor restServerInterceptor=new  RestServerInterceptor();
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(restServerInterceptor).addPathPatterns("/demo2/**");
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
