package pl.koder95.sbp.backend.dto;

import java.time.ZonedDateTime;
import pl.koder95.sbp.backend.model.DeliveryStatus;

public record EmailDeliveryInfoDto(
        ZonedDateTime createdAt,
        DeliveryStatus status,
        String error,
        String recipient
) {
}
