package pl.koder95.sbp.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.time.ZoneId;

public record GenerateOneTimeTokenRequestDto(
        @NotNull @Email String email,
        ZoneId zoneId
) {
}
