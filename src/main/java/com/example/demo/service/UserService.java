package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.enums.AuthProvider;
import com.example.demo.model.enums.UserRole;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerPatient(String name, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered. Please login instead.");
        }
        User user = new User(name, email, passwordEncoder.encode(password), UserRole.PATIENT, AuthProvider.LOCAL);
        return userRepository.save(user);
    }

    public User findOrCreateGoogleUser(String name, String email, String picture, String googleId) {
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            User user = existing.get();
            // Update profile info from Google
            user.setName(name);
            user.setPicture(picture);
            if (user.getProvider() == AuthProvider.LOCAL) {
                // Link Google to existing local account
                user.setProvider(AuthProvider.GOOGLE);
                user.setProviderId(googleId);
            }
            return userRepository.save(user);
        }
        // Create new Google user
        User user = new User(name, email, null, UserRole.PATIENT, AuthProvider.GOOGLE);
        user.setPicture(picture);
        user.setProviderId(googleId);
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean checkPassword(User user, String rawPassword) {
        if (user.getPassword() == null) return false;
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}
