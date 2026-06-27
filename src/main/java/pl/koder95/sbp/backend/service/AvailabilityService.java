package pl.koder95.sbp.backend.service;

import java.util.UUID;
import pl.koder95.sbp.backend.dto.AvailabilityDto;
import pl.koder95.sbp.backend.dto.UpdateAvailabilityRequestDto;

public interface AvailabilityService {
    AvailabilityDto getFor(UUID teacherUuid);

    void createEmptyFor(UUID teacherUuid);

    AvailabilityDto updateFor(UUID teacherUuid, UpdateAvailabilityRequestDto dto, boolean patch);

    default AvailabilityDto updateFor(UUID teacherUuid, UpdateAvailabilityRequestDto dto) {
        return updateFor(teacherUuid, dto, false);
    }

    void deleteFor(UUID teacherUuid);
}
