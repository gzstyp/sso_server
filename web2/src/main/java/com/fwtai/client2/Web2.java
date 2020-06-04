package com.fwtai.client2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//必须先启动认证服务器端或资源服务器才不报错
@SpringBootApplication
public class Web2{

    public static void main(String[] args) {
        SpringApplication.run(Web2.class, args);
    }
}