
package com.driveease.gateway.service;

import java.util.Locale;
import java.util.regex.Pattern;

import com.driveease.gateway.entity.AppUser;
import com.driveease.gateway.repository.AppUserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public AuthService(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AppUser registerUser(
            String username, String email, String password) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }

        String normalizedUsername = username.trim();

        if (normalizedUsername.length() > 100) {
            throw new IllegalArgumentException(
                    "Username must not exceed 100 characters.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        if (normalizedEmail.length() > 254
                || !EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw new IllegalArgumentException(
                    "Please provide a valid email address.");
        }

        if (password == null || password.length() < 12) {
            throw new IllegalArgumentException(
                    "Password must contain at least 12 characters.");
        }

        if (appUserRepository.existsByUsername(normalizedUsername)) {
            throw new IllegalArgumentException(
                    "Username is already registered.");
        }

        if (appUserRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException(
                    "Email is already registered.");
        }

        String passwordHash = passwordEncoder.encode(password);

        // Public registration always creates a USER.
        AppUser user = new AppUser(
                normalizedUsername,
                normalizedEmail,
                passwordHash,
                "USER"
        );

        return appUserRepository.save(user);
    }

    public AppUser authenticate(String usernameOrEmail, String password) {
        if (usernameOrEmail == null || usernameOrEmail.isBlank()
                || password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                    "Invalid username/email or password.");
        }

        String identifier = usernameOrEmail.trim();

        AppUser user = identifier.contains("@")
                ? appUserRepository.findByEmailIgnoreCase(identifier)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Invalid username/email or password."))
                : appUserRepository.findByUsername(identifier)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Invalid username/email or password."));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException(
                    "Invalid username/email or password.");
        }

        return user;
    }
}
