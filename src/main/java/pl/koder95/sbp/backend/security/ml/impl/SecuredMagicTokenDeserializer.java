package pl.koder95.sbp.backend.security.ml.impl;

import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.koder95.sbp.backend.config.MagicLinkConfig;
import pl.koder95.sbp.backend.security.ml.MagicToken;
import pl.koder95.sbp.backend.security.ml.MagicTokenDeserializer;

@Component
@RequiredArgsConstructor
public class SecuredMagicTokenDeserializer implements MagicTokenDeserializer {
    private final MagicLinkConfig config;

    private Cipher cipher;

    @Override
    public MagicToken deserialize(String token) {
        String decoded = new String(Base64.getUrlDecoder().decode(token));
        if (decoded.isBlank() || !decoded.contains(":::")) {
            throw new IllegalArgumentException("Invalid token");
        }
        String[] split = decoded.split(":::");
        if (split.length != 3) {
            throw new IllegalArgumentException("Invalid token");
        }
        try {
            String email = decode(split[0]);
            String createdAt = decode(split[1]);
            UUID notificationUuid = UUID.fromString(decode(split[2]));
            return new MagicToken(email, ZonedDateTime.parse(createdAt), notificationUuid);
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
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
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

    private String decode(String part) throws Exception {
        byte[] secured = Base64.getDecoder().decode(part);
        if (cipher == null) {
            initCipher();
        }
        byte[] bytes = cipher.doFinal(secured);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
