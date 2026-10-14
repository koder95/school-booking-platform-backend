package pl.koder95.sbp.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateTeacherAvatarRequestDto(
        @NotBlank String avatarUrl
) {
}
