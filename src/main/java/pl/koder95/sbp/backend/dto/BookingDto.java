package pl.koder95.sbp.backend.dto;

import java.util.UUID;

public record BookingDto(
        UUID uuid, UUID studentUuid, UUID lessonUuid, String bookedAt, BookingType type
) {
    public BookingDto {
        type = type == null ? BookingType.ACCEPTED : BookingType.REQUESTED;
    }
}
