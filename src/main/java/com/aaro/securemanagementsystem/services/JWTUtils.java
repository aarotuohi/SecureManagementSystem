package com.aaro.securemanagementsystem.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Component
public class JWTUtils {

    private static final Logger log = LoggerFactory.getLogger(JWTUtils.class);
    private SecretKey key;
    private static final long EXPIRATION_TIME = 86_400_000L;  // 24 hours

    @Value("${jwt.secret:}")
    private String configuredSecret;

    @PostConstruct
    void initKey() {
        byte[] secretBytes;
        if (configuredSecret != null && !configuredSecret.isBlank()) {
            try {
                // Prefer Base64 secrets for portability
                secretBytes = Base64.getDecoder().decode(configuredSecret);
                log.info("JWT secret loaded from configuration (Base64)");
            } catch (IllegalArgumentException ex) {
                // Fallback: treat as raw text
                secretBytes = configuredSecret.getBytes(StandardCharsets.UTF_8);
                log.warn("JWT secret is not Base64; using raw bytes. Consider providing a Base64 secret.");
            }
        } else {
            // Development fallback: static but strong-enough key material; replace in prod via env
            String devFallback = "dev-only-fallback-secret-please-set-jwt.secret-env-var-32bytes-min";
            secretBytes = devFallback.getBytes(StandardCharsets.UTF_8);
            log.warn("No jwt.secret configured. Using development fallback secret. Configure 'jwt.secret' for production.");
        }

        if (secretBytes.length < 32) {
            log.warn("JWT secret is shorter than 32 bytes; HS256 requires >= 256-bit key. Tokens may be insecure.");
        }
        this.key = Keys.hmacShaKeyFor(secretBytes);
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(HashMap<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        return claimsTFunction.apply(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }
}