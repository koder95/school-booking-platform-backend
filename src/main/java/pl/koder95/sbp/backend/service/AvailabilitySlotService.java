package pl.koder95.sbp.backend.service;

import java.time.Period;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.koder95.sbp.backend.dto.AvailabilitySlotDto;

public interface AvailabilitySlotService {
    List<AvailabilitySlotDto> createOrGetFor(
            UUID teacherUuid, ZonedDateTime startTime, ZonedDateTime endTime
    );

    Page<AvailabilitySlotDto> createOrGetFor(
            UUID teacherUuid, ZonedDateTime startTime, ZonedDateTime endTime, Pageable pageable
    );

    default List<AvailabilitySlotDto> createOrGetFor(UUID teacherUuid, Period period) {
        ZonedDateTime now = ZonedDateTime.now();
        return createOrGetFor(teacherUuid, now, now.plus(period));
    }

    default Page<AvailabilitySlotDto> createOrGetFor(
            UUID teacherUuid, Period period, Pageable pageable
    ) {
        ZonedDateTime now = ZonedDateTime.now();
        return createOrGetFor(teacherUuid, now, now.plus(period), pageable);
    }

    List<AvailabilitySlotDto> getAllFor(UUID teacherUuid);

    Page<AvailabilitySlotDto> getAllFor(UUID teacherUuid, Pageable pageable);

    List<AvailabilitySlotDto> deleteAllFor(UUID teacherUuid);

    Page<AvailabilitySlotDto> deleteAllFor(UUID teacherUuid, Pageable pageable);
}
