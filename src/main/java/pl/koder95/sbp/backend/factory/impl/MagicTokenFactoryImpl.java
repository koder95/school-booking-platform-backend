package pl.koder95.sbp.backend.factory.impl;

import java.time.ZonedDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;
import pl.koder95.sbp.backend.factory.MagicTokenFactory;
import pl.koder95.sbp.backend.security.ml.MagicToken;

@Component
public class MagicTokenFactoryImpl implements MagicTokenFactory {
    @Override
    public MagicToken createMagicToken(String email, UUID notificationUuid) {
        if (email == null || notificationUuid == null) {
            throw new IllegalArgumentException("email and notification uuid cannot be null");
        }
        if (email.isBlank()) {
            throw new IllegalArgumentException("email cannot be blank");
        }
        return new MagicToken(email, ZonedDateTime.now(), notificationUuid);
    }
}
