package pl.koder95.sbp.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmailValueDto(
        @NotNull @NotBlank @Email String value
) {
}
