package pl.koder95.sbp.backend.service;

import java.util.UUID;
import pl.koder95.sbp.backend.dto.StudentAbilitiesDto;
import pl.koder95.sbp.backend.dto.UpdateStudentAbilitiesRequestDto;

public interface AbilitiesService {
    StudentAbilitiesDto getAbilities(UUID studentUuid);

    StudentAbilitiesDto updateAbilities(UUID studentUuid,
                                        UpdateStudentAbilitiesRequestDto requestDto);
}
