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
			// Create default admin account for easy login
			if (!userRepository.existsByEmail("admin@algomentor.com")) {
				User admin = new User();
				admin.setEmail("admin@algomentor.com");
				admin.setPassword(passwordEncoder.encode("admin"));
				admin.setRole("TEACHER");
				userRepository.save(admin);
				System.out.println("✅ Default admin account created - Username: admin@algomentor.com, Password: admin");
			}
		};
	}

}
