package ru.ryzhkov.webfluxsecurity.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.ryzhkov.webfluxsecurity.security.properties.PasswordEncoderProperties;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class PBFDK2Encoder implements PasswordEncoder {
    private final PasswordEncoderProperties properties;

    private final static String SECRET_KEY_INSTANCE = "PBKDF2WithHmacSHA512";

    @Override
    public String encode(CharSequence rawPassword) {
        try {
            byte[] encoded = SecretKeyFactory.getInstance(SECRET_KEY_INSTANCE)
                    .generateSecret(new PBEKeySpec(rawPassword.toString().toCharArray(),
                            properties.getSecret().getBytes(),
                            properties.getIteration(),
                            properties.getKeyLength())).getEncoded();
            return Base64.getEncoder().encodeToString(encoded);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}
