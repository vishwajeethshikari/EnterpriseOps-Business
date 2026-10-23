
package com.driveease.gateway.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.crypto.SecretKey;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import com.driveease.gateway.entity.AppUser;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;

    public JwtService(SecretKey jwtSecretKey) {
        this.jwtEncoder = new NimbusJwtEncoder(
                new ImmutableSecret<>(jwtSecretKey));
    }

    public String generateToken(AppUser user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("enterpriseops-business")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(user.getUsername())
                .claim("role", user.getRole())
                .claim("userId", user.getId().toString())
                .build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(
                        org.springframework.security.oauth2.jwt
                                .JwsHeader.with(MacAlgorithm.HS256).build(),
                        claims))
                .getTokenValue();
    }
    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }
}
