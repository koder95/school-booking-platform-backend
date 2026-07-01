package pl.koder95.sbp.backend.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record TimeRangeDto(
        @NotNull LocalTime startTime, @NotNull LocalTime endTime
) {
}
