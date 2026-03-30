package com.fooddelivery.deliveryfee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DeliveryfeeApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeliveryfeeApplication.class, args);
	}

}
