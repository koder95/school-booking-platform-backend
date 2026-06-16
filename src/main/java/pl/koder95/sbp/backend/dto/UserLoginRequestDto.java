package pl.koder95.sbp.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserLoginRequestDto(
        @NotNull @NotBlank @Email
        String email,
        @NotNull @NotBlank
        String password
) {
}
