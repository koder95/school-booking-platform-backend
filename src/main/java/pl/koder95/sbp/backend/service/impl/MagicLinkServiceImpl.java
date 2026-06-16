package pl.koder95.sbp.backend.service.impl;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.koder95.sbp.backend.config.MagicLinkConfig;
import pl.koder95.sbp.backend.dto.EmailValueDto;
import pl.koder95.sbp.backend.exception.InvalidMagicTokenException;
import pl.koder95.sbp.backend.factory.MagicLinkFactory;
import pl.koder95.sbp.backend.security.ml.MagicLink;
import pl.koder95.sbp.backend.security.ml.MagicToken;
import pl.koder95.sbp.backend.security.ml.MagicTokenDeserializer;
import pl.koder95.sbp.backend.service.MagicLinkService;

@Service
@RequiredArgsConstructor
public class MagicLinkServiceImpl implements MagicLinkService {
    private final MagicLinkFactory magicLinkFactory;
    private final MagicLinkConfig magicLinkConfig;
    private final MagicTokenDeserializer magicTokenDeserializer;

    @Override
    public MagicLink createMagicLink(EmailValueDto dto, UUID notificationUuid) {
        if (dto == null || notificationUuid == null) {
            throw new IllegalArgumentException("dto and notification uuid cannot be null");
        }
        return magicLinkFactory.createMagicLink(dto.value(), notificationUuid);
    }

    @Override
    public void validateMagicToken(String email, UUID notificationUuid, String token)
            throws InvalidMagicTokenException {
        try {
            token = Objects.requireNonNull(token);
            MagicToken deserialized = Objects.requireNonNull(
                    magicTokenDeserializer.deserialize(token)
            );
            ZonedDateTime createdAt = Objects.requireNonNull(deserialized.createdAt());
            boolean nonExpired = createdAt.plusSeconds(magicLinkConfig.expirationTime())
                    .isAfter(ZonedDateTime.now());
            if (!nonExpired) {
                throw new InvalidMagicTokenException("Token is expired");
            }
            if (email != null) {
                if (!email.equals(deserialized.email())) {
                    throw new InvalidMagicTokenException("Email address mismatch");
                }
            }
            if (notificationUuid != null) {
                if (!notificationUuid.equals(deserialized.notificationUuid())) {
                    throw new InvalidMagicTokenException("Notification uuid mismatch");
                }
            }
        } catch (RuntimeException e) {
            throw new InvalidMagicTokenException("Invalid magic token: " + token);
        }
    }
}
