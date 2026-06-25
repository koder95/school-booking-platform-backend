package pl.koder95.sbp.backend.dto;

public record UpdateTeacherRequestDto(
        String email, String firstName, String lastName
) {
}
