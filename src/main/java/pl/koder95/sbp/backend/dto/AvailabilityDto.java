package pl.koder95.sbp.backend.dto;

import java.util.UUID;

public record AvailabilityDto(
        UUID teacherUuid,
        TimeRangeDto monday,
        TimeRangeDto tuesday,
        TimeRangeDto wednesday,
        TimeRangeDto thursday,
        TimeRangeDto friday,
        TimeRangeDto lunchBreak
) {
}
