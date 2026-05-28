package com.gu_pereira.api_libary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ApiLibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiLibraryApplication.class, args);
    }

}