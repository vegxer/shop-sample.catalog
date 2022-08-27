package ru.vegxer.shopsample.catalog.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.vegxer.shopsample.catalog.exception.JwtInitializationException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.function.BiFunction;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtKeyProvider {
    private static final String KEY_TYPE = "PUBLIC";

    @Value("${app.jwt.key.public.path}")
    private String publicKeyPath;

    @Getter
    private PublicKey publicKey;

    @PostConstruct
    public void init() {
        publicKey = readKey(
            publicKeyPath,
            KEY_TYPE,
            this::publicKeySpec,
            this::publicKeyGenerator
        );
    }

    private <T extends Key> T readKey(String keyPath, String headerSpec, Function<String, EncodedKeySpec> keySpec,
                                      BiFunction<KeyFactory, EncodedKeySpec, T> keyGenerator) {
        try {
            String keyString = new String(Files.readAllBytes(Paths.get(keyPath)));
            keyString = keyString.replace("-----BEGIN " + headerSpec + " KEY-----", "");
            keyString = keyString.replace("-----END " + headerSpec + " KEY-----", "");
            keyString = keyString.replaceAll("\\s+", "");

            return keyGenerator.apply(KeyFactory.getInstance("RSA"), keySpec.apply(keyString));
        } catch(NoSuchAlgorithmException | IOException e) {
            throw new JwtInitializationException(e);
        }
    }

    private EncodedKeySpec publicKeySpec(String data) {
        return new X509EncodedKeySpec(Base64.getDecoder().decode(data));
    }

    private PublicKey publicKeyGenerator(KeyFactory kf, EncodedKeySpec spec) {
        try {
            return kf.generatePublic(spec);
        } catch(InvalidKeySpecException e) {
            throw new JwtInitializationException(e);
        }
    }
}
