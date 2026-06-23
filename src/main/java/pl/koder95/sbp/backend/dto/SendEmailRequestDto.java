package pl.koder95.sbp.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendEmailRequestDto(
        @NotBlank @Email String recipient,
        @NotBlank String subject,
        @NotBlank String body) {
}
