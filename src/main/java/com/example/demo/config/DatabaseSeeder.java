package com.example.demo.config;

import com.example.demo.model.User;
import com.example.demo.model.enums.AuthProvider;
import com.example.demo.model.enums.UserRole;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByEmail("admin@drmadhu.com")) {
                User admin = new User(
                        "Prof. Dr. Madhu Lata Rana",
                        "admin@drmadhu.com",
                        passwordEncoder.encode("admin123"),
                        UserRole.ADMIN,
                        AuthProvider.LOCAL
                );
                userRepository.save(admin);
                System.out.println("Default admin user created: admin@drmadhu.com / admin123");
            }
        };
    }
}
