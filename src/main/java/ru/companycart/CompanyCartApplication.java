package ru.companycart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CompanyCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(CompanyCartApplication.class, args);
    }
}