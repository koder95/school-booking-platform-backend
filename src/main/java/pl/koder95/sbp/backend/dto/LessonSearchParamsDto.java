package pl.koder95.sbp.backend.dto;

import java.time.Instant;

public record LessonSearchParamsDto(Instant from, Instant to, Boolean open, SubjectDto subject) {
}
