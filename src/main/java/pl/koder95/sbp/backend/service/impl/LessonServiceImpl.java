package pl.koder95.sbp.backend.service.impl;

import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.koder95.sbp.backend.dto.CreateLessonRequestDto;
import pl.koder95.sbp.backend.dto.LessonDto;
import pl.koder95.sbp.backend.dto.LessonSearchParamsDto;
import pl.koder95.sbp.backend.dto.UpdateLessonRequestDto;
import pl.koder95.sbp.backend.exception.EntityNotFoundException;
import pl.koder95.sbp.backend.mapper.LessonMapper;
import pl.koder95.sbp.backend.model.Authority;
import pl.koder95.sbp.backend.model.AvailabilitySlot;
import pl.koder95.sbp.backend.model.Lesson;
import pl.koder95.sbp.backend.model.Student;
import pl.koder95.sbp.backend.model.Teacher;
import pl.koder95.sbp.backend.model.User;
import pl.koder95.sbp.backend.repository.AvailabilitySlotRepository;
import pl.koder95.sbp.backend.repository.BookingRepository;
import pl.koder95.sbp.backend.repository.LessonRepository;
import pl.koder95.sbp.backend.repository.SubjectRepository;
import pl.koder95.sbp.backend.repository.TeacherRepository;
import pl.koder95.sbp.backend.security.AuthenticationUtil;
import pl.koder95.sbp.backend.service.LessonService;
import pl.koder95.sbp.backend.specification.LessonSpecification;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository repository;
    private final LessonMapper mapper;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final BookingRepository bookingRepository;
    private final AuthenticationUtil authenticationUtil;
    private final LessonSpecification.Builder specificationBuilder;

    @Override
    @Transactional
    public LessonDto create(CreateLessonRequestDto requestDto) {
        Lesson saved = repository.save(mapper.toModel(
                requestDto, availabilitySlotRepository, teacherRepository, subjectRepository
        ));
        Optional<AvailabilitySlot> slotOpt = availabilitySlotRepository
                .findById(requestDto.availabilitySlotUuid());
        if (slotOpt.isPresent()) {
            Teacher teacher = saved.getAssigned();
            AvailabilitySlot slot = slotOpt.get();
            slot.removeTeacher(teacher);
            if (slot.getTeachers() == null || slot.getTeachers().isEmpty()) {
                teacherRepository.findByAvailabilitySlot(slot)
                        .forEach(t -> t.getAvailabilitySlots().remove(slot));
                availabilitySlotRepository.delete(slot);
            }
        }
        return mapper.toDto(saved, bookingRepository);
    }

    @Override
    public Page<LessonDto> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(lesson -> mapper.toDto(lesson, bookingRepository));
    }

    @Override
    public LessonDto getByUuid(UUID lessonUuid) {
        return repository.findById(lessonUuid)
                .map(lesson -> mapper.toDto(lesson, bookingRepository))
                .orElseThrow(() -> new EntityNotFoundException(
                        "Lesson not found with uuid: " + lessonUuid
                ));
    }

    @Override
    public LessonDto update(UUID lessonUuid, UpdateLessonRequestDto requestDto) {
        Lesson lesson = repository.findById(lessonUuid).orElseThrow(
                () -> new EntityNotFoundException("Lesson not found with uuid: " + lessonUuid)
        );
        mapper.updateModel(lesson, requestDto, teacherRepository, subjectRepository);
        return mapper.toDto(repository.save(lesson), bookingRepository);
    }

    @Override
    public LessonDto deleteById(UUID lessonUuid) {
        Lesson lesson = repository.findById(lessonUuid).orElseThrow(
                () -> new EntityNotFoundException("Lesson not found with uuid: " + lessonUuid)
        );
        repository.delete(lesson);
        return mapper.toDto(lesson, bookingRepository);
    }

    @Override
    public Page<LessonDto> getAllBooked(Pageable pageable) {
        User user = authenticationUtil.getAuthenticated();
        if (user == null) {
            throw new AccessDeniedException("Access denied");
        } else if (user instanceof Student student) {
            return findAllBookedForStudent(student, pageable);
        } else if (user.getAuthority() == Authority.ROLE_ADMIN) {
            return findAllBookedForAdmin(pageable);
        }
        throw new AccessDeniedException("Access denied for user: " + user.getUuid());
    }

    @Override
    public Page<LessonDto> search(LessonSearchParamsDto params, Pageable pageable) {
        return repository.findAll(specificationBuilder.build(params), pageable)
                .map(lesson -> mapper.toDto(lesson, bookingRepository));
    }

    private Page<LessonDto> findAllBookedForAdmin(Pageable pageable) {
        return repository.findAllByBookingsNotEmpty(pageable)
                .map(lesson -> mapper.toDto(lesson, bookingRepository));
    }

    private Page<LessonDto> findAllBookedForStudent(Student student, Pageable pageable) {
        return repository.findAllByBookingsOfStudent(student, pageable)
                .map(lesson -> mapper.toDto(lesson, bookingRepository));
    }
}
