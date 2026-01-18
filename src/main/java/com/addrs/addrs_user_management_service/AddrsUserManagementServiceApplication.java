package com.addrs.addrs_user_management_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class AddrsUserManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AddrsUserManagementServiceApplication.class, args);
	}

}
