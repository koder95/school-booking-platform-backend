package pl.koder95.sbp.backend.dto;

import java.util.UUID;

public record TeacherDto(
        UUID uuid, long emailId, String firstName, String lastName
) {
}
