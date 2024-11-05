package com.example.DemoVirtualThreads;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class DemoVirtualThreadsApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(DemoVirtualThreadsApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(DemoVirtualThreadsApplication.class);
    }
}
