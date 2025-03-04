package com.bit.joe.shoppingmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BitApplication {

    public static void main(String[] args) {
        SpringApplication.run(BitApplication.class, args);
    }
}
