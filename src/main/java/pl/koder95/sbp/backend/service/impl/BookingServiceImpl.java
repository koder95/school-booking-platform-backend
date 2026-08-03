package pl.koder95.sbp.backend.service.impl;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.koder95.sbp.backend.dto.BookingDto;
import pl.koder95.sbp.backend.exception.EntityNotFoundException;
import pl.koder95.sbp.backend.exception.IllegalBookingException;
import pl.koder95.sbp.backend.mapper.BookingMapper;
import pl.koder95.sbp.backend.model.Booking;
import pl.koder95.sbp.backend.model.Lesson;
import pl.koder95.sbp.backend.model.Student;
import pl.koder95.sbp.backend.repository.BookingRepository;
import pl.koder95.sbp.backend.repository.LessonRepository;
import pl.koder95.sbp.backend.security.AuthenticationUtil;
import pl.koder95.sbp.backend.service.BookingService;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final BookingMapper mapper;
    private final LessonRepository lessonRepository;
    private final AuthenticationUtil authenticationUtil;

    @Override
    @Transactional
    public BookingDto book(UUID lessonUuid) {
        Student student = Optional.ofNullable(authenticationUtil.getAuthenticatedStudent())
                .orElseThrow(() -> new IllegalBookingException(
                        "booking requires authentication by student"
                ));
        Lesson lesson = lessonRepository.findById(lessonUuid).orElseThrow(
                () -> new EntityNotFoundException("lesson not found: " + lessonUuid)
        );
        Booking created = new Booking(student, lesson);
        if (created.getBookedAt().isAfter(lesson.getClosingTime())) {
            throw new IllegalBookingException("booking was closed for lesson: " + lessonUuid);
        }
        long enrolled = repository.countDistinctByLesson(lesson);
        if (enrolled >= lesson.getMaxEnrolled()) {
            throw new IllegalBookingException("no more free slots for lesson: " + lessonUuid);
        }
        return mapper.toDto(repository.save(created));
    }
}
