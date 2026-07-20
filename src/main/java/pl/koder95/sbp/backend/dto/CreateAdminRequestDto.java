package pl.koder95.sbp.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.ZoneId;

public record CreateAdminRequestDto(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 15) String password,
        ZoneId zoneId
) {
}
