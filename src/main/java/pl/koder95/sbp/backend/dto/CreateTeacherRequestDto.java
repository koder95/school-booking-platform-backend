package pl.koder95.sbp.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.ZoneId;

public record CreateTeacherRequestDto(
        @NotBlank @Email String email,
        @NotNull @Positive Long subjectId,
        String firstName, String lastName, ZoneId zoneId
) {
}
