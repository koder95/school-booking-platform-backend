package pl.koder95.sbp.backend.dto;

import java.time.LocalDate;
import java.util.UUID;
import pl.koder95.sbp.backend.model.WorkType;

public record TeacherDtoWithoutEmail(
        UUID uuid, String firstName, String lastName, SubjectDto subjectDto,
        String color, WorkType workType, LocalDate workDueDate,
        String avatar
) {
    public TeacherDtoWithoutEmail {
        color = color == null || color.isBlank() ? generateColorHex() : color;
    }

    private String generateColorHex() {
        return "#%06x".formatted(Math.round(Math.random() * 0xffffff));
    }
}
