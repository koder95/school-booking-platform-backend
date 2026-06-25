package pl.koder95.sbp.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTeacherRequestDto(
        @NotBlank String email,
        String firstName, String lastName
) {
}
