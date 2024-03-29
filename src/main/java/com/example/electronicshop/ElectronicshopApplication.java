package com.example.electronicshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableMongoAuditing
@EnableAsync(proxyTargetClass = true)
@EnableScheduling
public class ElectronicshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElectronicshopApplication.class, args);
	}

}
