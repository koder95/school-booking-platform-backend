package pl.koder95.sbp.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateTeacherRequestDto(
        @NotBlank @Email String email, String firstName, String lastName
) {
}
