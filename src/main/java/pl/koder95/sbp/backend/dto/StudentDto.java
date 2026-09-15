package pl.koder95.sbp.backend.dto;

import java.time.ZoneId;
import java.util.UUID;

public record StudentDto(
        UUID uuid,
        String email,
        String avatarUrl,
        ZoneId zoneId,
        boolean isTrial
) {
}
