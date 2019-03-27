package com.guoxc.info.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@SpringBootApplication
@MapperScan("com.guoxc.info.dao")
public class WebApplication {

	public static void main(String[] args) {

		 SpringApplication.run(WebApplication.class, args);

	}

}
