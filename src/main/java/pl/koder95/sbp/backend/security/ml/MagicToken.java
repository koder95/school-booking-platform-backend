package pl.koder95.sbp.backend.security.ml;

import java.time.ZonedDateTime;
import java.util.UUID;

public record MagicToken(
        String email,
        ZonedDateTime createdAt,
        UUID notificationUuid
) {
}
