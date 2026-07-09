package pl.koder95.sbp.backend.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.ZoneId;

public record CreateStudentRequestDto(
        @NotBlank String email,
        ZoneId zoneId
) {
}
