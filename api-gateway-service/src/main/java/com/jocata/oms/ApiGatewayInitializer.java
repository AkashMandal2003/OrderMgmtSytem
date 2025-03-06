package com.jocata.oms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class ApiGatewayInitializer {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayInitializer.class, args);
    }
}