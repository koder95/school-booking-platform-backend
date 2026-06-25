package pl.koder95.sbp.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateSubjectRequestDto(
        @NotBlank String name,
        String description
) {
}
