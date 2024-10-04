package com.forex;

import org.modelmapper.internal.bytebuddy.implementation.bind.annotation.Empty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ForExRateApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForExRateApplication.class, args);
	}

}
