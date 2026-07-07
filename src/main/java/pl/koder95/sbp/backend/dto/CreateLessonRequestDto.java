package pl.koder95.sbp.backend.dto;

import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateLessonRequestDto(
        @NotNull UUID availabilitySlotUuid,
        @NotNull UUID teacherUuid,
        Integer maxEnrolled
) {
    public CreateLessonRequestDto {
        maxEnrolled = maxEnrolled == null ? 1 : maxEnrolled;
        if (maxEnrolled < 1) {
            throw new ValidationException("maxEnrolled must be greater than 0");
        }
    }
}
