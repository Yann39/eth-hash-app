package com.example.ethhashapp;

import com.example.ethhashapp.config.ApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Yann39
 * @since 1.0.0
 */
@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({
        ApplicationConfig.class
})
public class EthHashApp {

    public static void main(String[] args) {
        SpringApplication.run(EthHashApp.class, args);
    }

}