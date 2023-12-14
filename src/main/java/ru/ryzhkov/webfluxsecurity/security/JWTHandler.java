package ru.ryzhkov.webfluxsecurity.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import reactor.core.publisher.Mono;
import ru.ryzhkov.webfluxsecurity.exception.AuthException;
import ru.ryzhkov.webfluxsecurity.exception.UnauthorizedException;

import java.util.Base64;
import java.util.Date;

public class JWTHandler {
    private final String secret;

    public JWTHandler(String secret) {
        this.secret = secret;
    }

    public Mono<VerificationResult> check(String accessToken) {
        return Mono.just(verify(accessToken))
                .onErrorResume(e -> Mono.error(new UnauthorizedException(e.getMessage())));
    }

    private VerificationResult verify(String token) {
        Claims claims = getClaimsFromToken(token);
        final Date expirationDate = claims.getExpiration();

        if (expirationDate.before(new Date())) {
            throw new AuthException("Token expired", "TOKEN_EXPIRED");
        }

        return new VerificationResult(claims, token);
    }

    private Claims getClaimsFromToken(String token) {
        String encodedSecret = Base64.getEncoder().encodeToString(secret.getBytes());
        return Jwts.parser()
                .setSigningKey(encodedSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    public static class VerificationResult {
        public Claims claims;
        public String token;

        public VerificationResult(Claims claims, String token) {
            this.claims = claims;
            this.token = token;
        }
    }
}
