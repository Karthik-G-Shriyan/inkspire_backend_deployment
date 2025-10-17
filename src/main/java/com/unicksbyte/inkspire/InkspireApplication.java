package com.unicksbyte.inkspire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class InkspireApplication {

	public static void main(String[] args) {
		SpringApplication.run(InkspireApplication.class, args);
	}

}
