package pl.koder95.sbp.backend.dto;

import jakarta.validation.Valid;

public record UpdateAvailabilityRequestDto(
        @Valid TimeRangeDto monday,
        @Valid TimeRangeDto tuesday,
        @Valid TimeRangeDto wednesday,
        @Valid TimeRangeDto thursday,
        @Valid TimeRangeDto friday,
        @Valid TimeRangeDto lunchBreak
) {
}
