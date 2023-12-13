package ru.ryzhkov.webfluxsecurity.security;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "jwt.password.encoder")
public class JWTProperties {
    private String secret;
    private Integer iteration;
    private Integer keyLength;
}
