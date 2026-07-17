package pl.koder95.sbp.backend.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import pl.koder95.sbp.backend.service.BookingService;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final BookingMapper mapper;
    private final LessonRepository lessonRepository;

    @Override
    @Transactional
    public BookingDto book(UUID lessonUuid) {
        Student student = getAuthenticatedStudent();
        Lesson lesson = lessonRepository.findById(lessonUuid).orElseThrow(
                () -> new EntityNotFoundException("lesson not found")
        );
        Booking created = new Booking(student, lesson, LocalDateTime.now());
        if (created.getBookedAt().isAfter(lesson.getClosingTime())) {
            throw new IllegalBookingException("booking was closed");
        }
        long enrolled = repository.countDistinctByLesson(lesson);
        if (enrolled >= lesson.getMaxEnrolled()) {
            throw new IllegalBookingException("no more free slots");
        }
        return mapper.toDto(repository.save(created));
    }

    private Student getAuthenticatedStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalBookingException("booking is not available for public access");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Student student) {
            return student;
        }
        throw new IllegalBookingException("booking is not available for current user");
    }
}
