package pl.koder95.sbp.backend.dto;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;
import pl.koder95.sbp.backend.model.WorkType;

public record TeacherDto(
        UUID uuid, String email, SubjectDto subject,
        String firstName, String lastName, ZoneId zoneId,
        String color, WorkType workType, LocalDate workDueDate
) {
    public TeacherDto {
        color = color == null || color.isBlank() ? generateColorHex() : color;
    }

    private String generateColorHex() {
        return "#%06x".formatted(Math.round(Math.random() * 0xffffff));
    }
}
