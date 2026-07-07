package pl.koder95.sbp.backend.dto;

import java.time.ZoneId;
import java.util.UUID;

public record TeacherDto(
        UUID uuid, long emailId, long subjectId,
        String firstName, String lastName, ZoneId zoneId
) {
}
