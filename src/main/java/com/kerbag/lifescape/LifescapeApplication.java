package com.kerbag.lifescape;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class LifescapeApplication {
	public static void main(String[] args) {
		SpringApplication.run(LifescapeApplication.class, args);
	}

}
