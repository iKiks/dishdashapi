package com.kiks.dishdashapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DishdashapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DishdashapiApplication.class, args);
    }

}
