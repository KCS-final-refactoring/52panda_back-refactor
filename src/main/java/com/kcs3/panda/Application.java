package com.kcs3.panda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableScheduling
@EnableCaching // 레디스 캐싱 하기위해 추가, @Cacheable 같은 어노테이션을 인식 하게 함
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
