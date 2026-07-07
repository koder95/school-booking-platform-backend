package pl.koder95.sbp.backend.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.koder95.sbp.backend.dto.CreateLessonRequestDto;
import pl.koder95.sbp.backend.dto.LessonDto;
import pl.koder95.sbp.backend.dto.UpdateLessonRequestDto;
import pl.koder95.sbp.backend.exception.EntityNotFoundException;
import pl.koder95.sbp.backend.mapper.LessonMapper;
import pl.koder95.sbp.backend.model.Lesson;
import pl.koder95.sbp.backend.repository.AvailabilitySlotRepository;
import pl.koder95.sbp.backend.repository.LessonRepository;
import pl.koder95.sbp.backend.repository.SubjectRepository;
import pl.koder95.sbp.backend.repository.TeacherRepository;
import pl.koder95.sbp.backend.service.LessonService;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository repository;
    private final LessonMapper mapper;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;

    @Override
    @Transactional
    public LessonDto create(CreateLessonRequestDto requestDto) {
        Lesson saved = repository.save(mapper.toModel(
                requestDto, availabilitySlotRepository, teacherRepository, subjectRepository
        ));
        availabilitySlotRepository.deleteById(requestDto.availabilitySlotUuid());
        return mapper.toDto(saved);
    }

    @Override
    public Page<LessonDto> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public LessonDto getByUuid(UUID lessonUuid) {
        return repository.findById(lessonUuid).map(mapper::toDto).orElseThrow(
                () -> new EntityNotFoundException("Lesson not found with uuid: " + lessonUuid)
        );
    }

    @Override
    public LessonDto update(UUID lessonUuid, UpdateLessonRequestDto requestDto) {
        Lesson lesson = repository.findById(lessonUuid).orElseThrow(
                () -> new EntityNotFoundException("Lesson not found with uuid: " + lessonUuid)
        );
        mapper.updateModel(lesson, requestDto, teacherRepository, subjectRepository);
        return mapper.toDto(repository.save(lesson));
    }

    @Override
    public LessonDto deleteById(UUID lessonUuid) {
        Lesson lesson = repository.findById(lessonUuid).orElseThrow(
                () -> new EntityNotFoundException("Lesson not found with uuid: " + lessonUuid)
        );
        repository.delete(lesson);
        return mapper.toDto(lesson);
    }
}
