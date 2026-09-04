package pl.koder95.sbp.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.ZoneId;
import pl.koder95.sbp.backend.model.WorkType;

public record CreateTeacherRequestDto(
        @NotBlank @Email String email,
        @NotNull @Positive Long subjectId,
        String firstName, String lastName, ZoneId zoneId,
        @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$") String color,
        WorkType workType, LocalDate workDueDate
) {
}
