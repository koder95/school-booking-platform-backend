package pl.koder95.sbp.backend.factory.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.koder95.sbp.backend.dto.AvailabilityDto;
import pl.koder95.sbp.backend.dto.TimeRangeDto;
import pl.koder95.sbp.backend.factory.LocalDateFactory;
import pl.koder95.sbp.backend.factory.LocalTimeFactory;
import pl.koder95.sbp.backend.factory.ZonedDateTimeFactory;

@Component
@RequiredArgsConstructor
public class ZonedDateTimeFactoryImpl implements ZonedDateTimeFactory {
    private final LocalDateFactory dateFactory;
    private final LocalTimeFactory timeFactory;

    @Override
    public List<ZonedDateTime> createTimestampsWith(ZoneId zoneId, Period period,
                                                    AvailabilityDto teacherAvailability) {
        ZonedDateTime startTime = ZonedDateTime.now(zoneId);
        LocalDate startDate = startTime.toLocalDate();
        return dateFactory.createDatesForPeriod(startDate, startDate.plus(period)).stream()
                .flatMap(date -> buildLocalDateTimes(date, teacherAvailability).stream())
                .map(local -> local.atZone(zoneId))
                .toList();
    }

    private Optional<TimeRangeDto> workTimeRange(LocalDate localDate, AvailabilityDto dto) {
        return switch (localDate.getDayOfWeek()) {
            case MONDAY -> Optional.of(dto.monday());
            case TUESDAY -> Optional.of(dto.tuesday());
            case WEDNESDAY -> Optional.of(dto.wednesday());
            case THURSDAY -> Optional.of(dto.thursday());
            case FRIDAY -> Optional.of(dto.friday());
            default -> Optional.empty();
        };
    }

    private TimeRangeDto breakTimeRange(AvailabilityDto teacherAvailability) {
        return teacherAvailability.lunchBreak();
    }

    private List<LocalDateTime> buildLocalDateTimes(LocalDate date, AvailabilityDto dto) {
        Optional<TimeRangeDto> workTimeRange = workTimeRange(date, dto);
        if (workTimeRange.isEmpty()) {
            return List.of();
        }
        TimeRangeDto timeRangeDto = workTimeRange.get();
        return buildLocalDateTimes(date, timeRangeDto, breakTimeRange(dto));
    }

    private List<LocalDateTime> buildLocalDateTimes(
            LocalDate localDate, TimeRangeDto workTimeRange, TimeRangeDto breakTimeRange
    ) {
        return buildLocalDateTimes(localDate, workTimeRange.startTime(), workTimeRange.endTime(),
                breakTimeRange.startTime(), breakTimeRange.endTime());
    }

    private List<LocalDateTime> buildLocalDateTimes(LocalDate localDate,
                                                    LocalTime startWork, LocalTime endWork,
                                                    LocalTime startBreak, LocalTime endBreak) {
        ArrayList<LocalTime> times = new ArrayList<>(
                timeFactory.createTimestampsBetween(startWork, endWork.minusSeconds(1)).stream()
                        .toList()
        );
        while (startBreak.isBefore(endBreak)) {
            times.remove(startBreak);
            startBreak = startBreak.plusHours(1);
        }
        return times.stream().map(time -> time.atDate(localDate)).toList();
    }
}
