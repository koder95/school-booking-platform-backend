package pl.koder95.sbp.backend.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public record LessonDto(
        UUID uuid,
        ZonedDateTime startTime,
        Long subjectId,
        UUID teacherUuid,
        Integer maxEnrolled,
        Integer enrolled,
        ZonedDateTime closingTime
) {
}
