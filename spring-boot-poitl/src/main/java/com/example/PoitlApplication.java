package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PoitlApplication {
    private static final Logger logger = LoggerFactory.getLogger(PoitlApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PoitlApplication.class, args);
    }
}
