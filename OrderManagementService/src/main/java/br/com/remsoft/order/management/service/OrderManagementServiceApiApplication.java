package br.com.remsoft.order.management.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class OrderManagementServiceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderManagementServiceApiApplication.class, args);
	}

}
