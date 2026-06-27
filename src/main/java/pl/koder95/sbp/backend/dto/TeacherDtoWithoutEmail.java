package pl.koder95.sbp.backend.dto;

import java.util.UUID;

public record TeacherDtoWithoutEmail(
        UUID uuid, String firstName, String lastName
) {
}
