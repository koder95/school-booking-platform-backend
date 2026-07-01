package pl.koder95.sbp.backend.dto;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

public record AvailabilitySlotDto(
        UUID uuid, ZonedDateTime timestamp, Set<TeacherDtoWithoutEmail> teachers
) {
}
