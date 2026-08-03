package pl.koder95.sbp.backend.dto;

import java.util.Set;
import java.util.UUID;

public record BookingDecisionDto(Set<UUID> accepted, Set<UUID> rejected) {
}
