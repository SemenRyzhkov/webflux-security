package ru.ryzhkov.webfluxsecurity.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.ryzhkov.webfluxsecurity.entity.UserEntity;
import ru.ryzhkov.webfluxsecurity.exception.AuthException;
import ru.ryzhkov.webfluxsecurity.repositiry.UserRepository;
import ru.ryzhkov.webfluxsecurity.security.properties.JWTProperties;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JWTProperties properties;

    private TokenDetails generateToken(UserEntity userEntity) {
        HashMap<String, Object> claims = new HashMap<>() {{
            put("role", userEntity.getRole());
        }};

        return generateToken(claims, userEntity.getId().toString());
    }

    private TokenDetails generateToken(Map<String, Object> claims, String subject) {
        long expirationTimeInMillis = properties.getExpiration() * 1000L;
        Date expirationDate = new Date(new Date().getTime() + expirationTimeInMillis);

        return generateToken(expirationDate, claims, subject);
    }

    private TokenDetails generateToken(
            Date expirationDate, Map<String, Object> claims, String subject
    ) {
        Date createdAt = new Date();
        String base64EncodedSecretKey = Base64.getEncoder().encodeToString(properties.getSecret().getBytes());
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(properties.getIssuer())
                .setSubject(subject)
                .setIssuedAt(createdAt)
                .setId(UUID.randomUUID().toString())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, base64EncodedSecretKey)
                .compact();

        return TokenDetails.builder()
                .token(token)
                .issuedAt(createdAt)
                .expiresAt(expirationDate)
                .build();
    }

    public Mono<TokenDetails> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .flatMap(user -> {
                    if (!user.isEnabled()) {
                        return Mono.error(new AuthException("Account disabled", "ACCOUNT_DISABLED"));
                    }
                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        return Mono.error(new AuthException("Invalid password", "INVALID_PASSWORD"));
                    }
                    return Mono.just(generateToken(user).toBuilder()
                            .userId(user.getId())
                            .build());
                })
                .switchIfEmpty(Mono.error(new AuthException("Invalid username", "INVALID_USERNAME")));
    }
}
