package pl.koder95.sbp.backend.service;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.koder95.sbp.backend.dto.CreateLessonRequestDto;
import pl.koder95.sbp.backend.dto.LessonDto;
import pl.koder95.sbp.backend.dto.LessonSearchParamsDto;
import pl.koder95.sbp.backend.dto.UpdateLessonRequestDto;

public interface LessonService {
    LessonDto create(CreateLessonRequestDto requestDto);

    Page<LessonDto> getAll(Pageable pageable);

    LessonDto getByUuid(UUID lessonUuid);

    LessonDto update(UUID lessonUuid, UpdateLessonRequestDto requestDto);

    LessonDto deleteById(UUID lessonUuid);

    Page<LessonDto> getAllBooked(Pageable pageable);

    Page<LessonDto> search(LessonSearchParamsDto params, Pageable pageable);
}
