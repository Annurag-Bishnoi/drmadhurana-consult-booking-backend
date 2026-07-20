package com.example.demo.config;

import com.example.demo.model.User;
import com.example.demo.model.enums.AuthProvider;
import com.example.demo.model.enums.UserRole;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@drloverpreet.com")) {
            User admin = new User(
                    "Dr. Loverpreet Singh",
                    "admin@drloverpreet.com",
                    passwordEncoder.encode("admin1234"),
                    UserRole.ADMIN,
                    AuthProvider.LOCAL
            );
            userRepository.save(admin);
            log.info("Admin account seeded: admin@drloverpreet.com / admin1234");
        } else {
            log.info("Admin account already exists. Skipping seed.");
        }
    }
}
