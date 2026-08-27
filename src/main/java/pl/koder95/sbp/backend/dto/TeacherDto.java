package pl.koder95.sbp.backend.dto;

import java.time.ZoneId;
import java.util.UUID;

public record TeacherDto(
        UUID uuid, String email, SubjectDto subject,
        String firstName, String lastName, ZoneId zoneId,
        String color
) {
    public TeacherDto {
        color = color == null || color.isBlank() ? generateColorHex() : color;
    }

    private String generateColorHex() {
        return "#%06x".formatted(Math.round(Math.random() * 0xffffff));
    }
}
