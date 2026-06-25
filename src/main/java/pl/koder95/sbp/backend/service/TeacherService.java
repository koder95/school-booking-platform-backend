package pl.koder95.sbp.backend.service;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.koder95.sbp.backend.dto.CreateTeacherRequestDto;
import pl.koder95.sbp.backend.dto.TeacherDto;
import pl.koder95.sbp.backend.dto.UpdateTeacherRequestDto;

public interface TeacherService {
    TeacherDto get(UUID uuid);

    Page<TeacherDto> getAll(Pageable pageable);

    TeacherDto create(CreateTeacherRequestDto requestDto);

    TeacherDto update(UUID uuid, UpdateTeacherRequestDto requestDto);

    TeacherDto delete(UUID uuid);
}
