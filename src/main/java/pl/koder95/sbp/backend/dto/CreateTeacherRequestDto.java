package pl.koder95.sbp.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.ZoneId;

public record CreateTeacherRequestDto(
        @NotBlank @Email String email, String firstName, String lastName, ZoneId zoneId
) {
}
