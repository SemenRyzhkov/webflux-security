package ru.ryzhkov.webfluxsecurity.security.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JWTProperties {
    private String secret;
    private Integer expiration;
    private String issuer;
}
