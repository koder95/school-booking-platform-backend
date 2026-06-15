package pl.koder95.sbp.backend.security.ml.impl;

import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.koder95.sbp.backend.config.MagicLinkConfig;
import pl.koder95.sbp.backend.security.ml.MagicToken;
import pl.koder95.sbp.backend.security.ml.MagicTokenSerializer;

@Component
@RequiredArgsConstructor
public class SecuredMagicTokenSerializer implements MagicTokenSerializer {
    private final MagicLinkConfig config;

    private Cipher cipher;

    @Override
    public String serialize(MagicToken magicToken) {
        if (magicToken == null) {
            throw new IllegalArgumentException("MagicToken cannot be null");
        }
        if (magicToken.email() == null
                || magicToken.createdAt() == null
                || magicToken.notificationUuid() == null) {
            throw new IllegalArgumentException("MagicToken fields cannot be null");
        }
        try {
            String value = "%s:::%s:::%s".formatted(
                    encode(magicToken.email()),
                    encode(magicToken.createdAt().toString()),
                    encode(magicToken.notificationUuid().toString())
            );
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initCipher() {
        SecretKey secretKey = Keys.hmacShaKeyFor(prepareKey());
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(new byte[16]);
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        } catch (NoSuchAlgorithmException | InvalidKeyException
                 | InvalidAlgorithmParameterException | NoSuchPaddingException e) {
            throw new RuntimeException("Cannot init cipher", e);
        }
    }

    private byte[] prepareKey() {
        try {
            return MessageDigest.getInstance("SHA-256")
                    .digest(config.secretKey().getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String encode(String part) throws Exception {
        byte[] bytes = part.getBytes(StandardCharsets.UTF_8);
        if (cipher == null) {
            initCipher();
        }
        byte[] secured = cipher.doFinal(bytes);
        return Base64.getEncoder().encodeToString(secured);
    }
}
