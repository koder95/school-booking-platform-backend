package pl.koder95.sbp.backend.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder(builderClassName = "Builder")
public record LessonSearchParamsDto(
        Instant from, Instant to, Boolean open, SubjectDto subject, UUID assignedTeacherUuid
) {
}
