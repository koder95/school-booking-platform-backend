package pl.koder95.sbp.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateTeacherRequestDto(
        @NotBlank @Email String email, String firstName, String lastName
) {
}
