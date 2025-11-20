package com.example.CanchaSystem;

import com.example.CanchaSystem.model.Admin;
import com.example.CanchaSystem.model.Role;
import com.example.CanchaSystem.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.beans.Encoder;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class CanchaSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CanchaSystemApplication.class, args);
	}
}
