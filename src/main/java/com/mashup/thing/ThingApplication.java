package com.mashup.thing;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class ThingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThingApplication.class, args);
    }

}
