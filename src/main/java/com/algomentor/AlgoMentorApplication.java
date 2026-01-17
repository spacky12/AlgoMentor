package com.algomentor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.algomentor.model.User;
import com.algomentor.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class AlgoMentorApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlgoMentorApplication.class, args);
	}

	@Bean
	public CommandLineRunner dataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (!userRepository.existsByEmail("teacher@algomentor.com")) {
				User teacher = new User();
				teacher.setEmail("teacher@algomentor.com");
				teacher.setPassword(passwordEncoder.encode("teacher123"));
				teacher.setRole("TEACHER");
				userRepository.save(teacher);
				System.out.println("Seeded default teacher: teacher@algomentor.com / teacher123");
			}
		};
	}

}
