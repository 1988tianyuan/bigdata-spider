package com.liugeng.bigdata.spider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BigdataSpiderApplication {

	public static void main(String[] args) {
		SpringApplication.run(BigdataSpiderApplication.class, args);
	}

}
