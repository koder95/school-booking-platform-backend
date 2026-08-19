package pl.koder95.sbp.backend.dto;

import java.util.UUID;

public record TeacherDtoWithoutEmail(
        UUID uuid, String firstName, String lastName, SubjectDto subjectDto,
        String color
) {
    public TeacherDtoWithoutEmail {
        color = color == null || color.isBlank() ? generateColorHex() : color;
    }

    private String generateColorHex() {
        return "#%06x".formatted(Math.round(Math.random() * 0xffffff));
    }
}
