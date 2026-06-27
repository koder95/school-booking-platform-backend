package pl.koder95.sbp.backend.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalTime;

public record TimeRangeDto(
        @NotBlank LocalTime startTime, @NotBlank LocalTime endTime
) {
}
