package pl.koder95.sbp.backend.dto;

import jakarta.validation.constraints.NotNull;
import java.time.ZoneId;

public record UpdateStudentRequestDto(
        @NotNull ZoneId zoneId,
        String avatarUrl,
        Boolean isTrial
) {
}
