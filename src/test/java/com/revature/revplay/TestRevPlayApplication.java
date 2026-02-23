package com.revature.revplay;

import org.springframework.boot.SpringApplication;

public class TestRevPlayApplication {

	public static void main(String[] args) {
		SpringApplication.from(RevPlayApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
