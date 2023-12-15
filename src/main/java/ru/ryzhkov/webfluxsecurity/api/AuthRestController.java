package ru.ryzhkov.webfluxsecurity.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.ryzhkov.webfluxsecurity.dto.AuthRequestDto;
import ru.ryzhkov.webfluxsecurity.dto.AuthResponseDto;
import ru.ryzhkov.webfluxsecurity.dto.UserDto;
import ru.ryzhkov.webfluxsecurity.entity.UserEntity;
import ru.ryzhkov.webfluxsecurity.mapper.UserMapper;
import ru.ryzhkov.webfluxsecurity.security.CustomPrincipal;
import ru.ryzhkov.webfluxsecurity.security.SecurityService;
import ru.ryzhkov.webfluxsecurity.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthRestController {
    private final SecurityService securityService;
    private final UserService userService;
    private final UserMapper userMapper;


    @PostMapping("/register")
    public Mono<UserDto> register(@RequestBody UserDto dto) {
        UserEntity entity = userMapper.mapToEntity(dto);
        return userService.registerUser(entity)
                .map(userMapper::mapToDto);
    }

    @PostMapping("/login")
    public Mono<AuthResponseDto> login(@RequestBody AuthRequestDto dto) {
        return securityService.authenticate(dto.getUsername(), dto.getPassword())
                .flatMap(tokenDetails -> Mono.just(
                        AuthResponseDto.builder()
                                .userId(tokenDetails.getUserId())
                                .token(tokenDetails.getToken())
                                .issuedAt(tokenDetails.getIssuedAt())
                                .expiresAt(tokenDetails.getExpiresAt())
                                .build()
                ));
    }

    @GetMapping("/info")
    public Mono<UserDto> getUserInfo(Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();

        return userService.getUserById(customPrincipal.getId())
                .map(userMapper::mapToDto);
    }
}
