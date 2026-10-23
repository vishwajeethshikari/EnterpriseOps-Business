
package com.driveease.gateway.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.driveease.gateway.entity.AppUser;
import com.driveease.gateway.security.JwtService;
import com.driveease.gateway.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(
            AuthService authService,
            JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    public record RegisterRequest(
            String username,
            String email,
            String password) {
    }

    public record LoginRequest(
            String usernameOrEmail,
            String password) {
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        try {
            if (request == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Request body is required."));
            }

            AppUser user = authService.registerUser(
                    request.username(),
                    request.email(),
                    request.password());

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of(
                            "message", "Registration successful",
                            "username", user.getUsername(),
                            "email", user.getEmail(),
                            "role", user.getRole()
                    )
            );

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        try {
            if (request == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Request body is required."));
            }

            AppUser user = authService.authenticate(
                    request.usernameOrEmail(),
                    request.password());

            String token = jwtService.generateToken(user);

            return ResponseEntity.ok(
                    Map.of(
                            "accessToken", token,
                            "tokenType", "Bearer",
                            "expiresIn", 3600,
                            "username", user.getUsername(),
                            "email", user.getEmail(),
                            "role", user.getRole()
                    )
            );

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error", "Invalid username/email or password."
                    ));
        }
    }
}
