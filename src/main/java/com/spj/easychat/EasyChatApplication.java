package com.spj.easychat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.spj.easychat.server.dao")
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class EasyChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyChatApplication.class, args);
    }

}
