package pl.koder95.sbp.backend.factory;

import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import pl.koder95.sbp.backend.dto.AvailabilityDto;

public interface ZonedDateTimeFactory {
    List<ZonedDateTime> createTimestampsWith(ZoneId zoneId, Period period,
                                             AvailabilityDto teacherAvailability);
}
