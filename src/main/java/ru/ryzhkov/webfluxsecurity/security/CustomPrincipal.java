package ru.ryzhkov.webfluxsecurity.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Principal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomPrincipal implements Principal {
    private Long id;
    private String name;

    @Override
    public String getName() {
        return null;
    }
}
