package pl.koder95.sbp.backend.dto;

import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.UUID;

public record UpdateLessonRequestDto(
        @NotNull Long subjectId,
        @NotNull UUID teacherUuid,
        @NotNull ZonedDateTime closingTime,
        Integer maxEnrolled
) {
    public UpdateLessonRequestDto {
        maxEnrolled = maxEnrolled == null ? 1 : maxEnrolled;
        if (maxEnrolled < 1) {
            throw new ValidationException("maxEnrolled must be greater than 0");
        }
    }
}
