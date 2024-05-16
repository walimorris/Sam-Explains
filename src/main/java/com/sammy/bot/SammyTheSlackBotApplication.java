package com.sammy.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
public class SammyTheSlackBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(SammyTheSlackBotApplication.class, args);
	}
}
