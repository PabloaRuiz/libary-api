package com.spring.libaryapi;

import com.spring.libaryapi.Service.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class LibaryApiApplication {

	@Autowired
	private EmailService emailService;

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public CommandLineRunner runner() {
		return args -> {
			List<String> emails = Arrays.asList("db3ca0f2a0-1c3b60+1@inbox.mailtrap.io");
			emailService.sendMails("Serviço de e-mail funcionando com sucesso!.", emails);
			System.out.println("E-mail enviado com sucesso");
		};
	}


	public static void main(String[] args) {
		SpringApplication.run(LibaryApiApplication.class, args);
	}

}
