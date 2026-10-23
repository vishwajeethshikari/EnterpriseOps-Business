
package com.driveease.gateway.service;

import com.driveease.gateway.entity.AppUser;
import com.driveease.gateway.repository.AppUserRepository;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrap {

    @Bean
    public ApplicationRunner createInitialAdmin(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            String username = System.getenv("INITIAL_ADMIN_USERNAME");
            String email = System.getenv("INITIAL_ADMIN_EMAIL");
            String password = System.getenv("INITIAL_ADMIN_PASSWORD");

            // Skip bootstrap when no administrator is configured.
            if (username == null && email == null && password == null) {
                return;
            }

            if (username == null || username.isBlank()
                    || email == null || email.isBlank()
                    || password == null || password.length() < 12) {
                throw new IllegalStateException(
                    "Configure INITIAL_ADMIN_USERNAME, "
                    + "INITIAL_ADMIN_EMAIL and "
                    + "INITIAL_ADMIN_PASSWORD. "
                    + "The password must contain at least 12 characters.");
            }

            username = username.trim();
            email = email.trim();

            var existingUser = userRepository.findByUsername(username);

            if (existingUser.isPresent()) {
                if (!"ADMIN".equals(existingUser.get().getRole())) {
                    throw new IllegalStateException(
                        "Initial admin username belongs to a non-admin account.");
                }

                return;
            }

            if (userRepository.existsByEmailIgnoreCase(email)) {
                throw new IllegalStateException(
                    "Initial admin email is already registered.");
            }

            AppUser admin = new AppUser(
                    username,
                    email,
                    passwordEncoder.encode(password),
                    "ADMIN"
            );

            userRepository.save(admin);
        };
    }
}
