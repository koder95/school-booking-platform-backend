package pl.koder95.sbp.backend.service.impl;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.koder95.sbp.backend.dto.BookingDecisionDto;
import pl.koder95.sbp.backend.dto.BookingDto;
import pl.koder95.sbp.backend.dto.SendEmailRequestDto;
import pl.koder95.sbp.backend.exception.EntityNotFoundException;
import pl.koder95.sbp.backend.exception.IllegalBookingException;
import pl.koder95.sbp.backend.mapper.BookingMapper;
import pl.koder95.sbp.backend.model.Booking;
import pl.koder95.sbp.backend.model.Lesson;
import pl.koder95.sbp.backend.model.Student;
import pl.koder95.sbp.backend.repository.BookingRepository;
import pl.koder95.sbp.backend.repository.LessonRepository;
import pl.koder95.sbp.backend.repository.StudentRepository;
import pl.koder95.sbp.backend.security.AuthenticationUtil;
import pl.koder95.sbp.backend.service.BookingService;
import pl.koder95.sbp.backend.service.EmailDeliveryService;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final BookingMapper mapper;
    private final LessonRepository lessonRepository;
    private final AuthenticationUtil authenticationUtil;
    private final EmailDeliveryService emailDeliveryService;
    private final StudentRepository studentRepository;

    private BookingDto bookAs(Student student, UUID lessonUuid) {
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
        Booking saved = repository.save(created);
        emailDeliveryService.send(new SendEmailRequestDto(
                student.getEmail().getValue(),
                "Booking status",
                createEmailBody(saved.getUuid(), lesson.getStartTime(), student.isTrial())
        ));
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public BookingDto book(UUID lessonUuid, UUID studentUuid) {
        Student student = studentRepository.findById(studentUuid).orElseThrow(
                () -> new EntityNotFoundException("student not found: " + studentUuid)
        );
        return bookAs(student, lessonUuid);
    }

    @Override
    @Transactional
    public BookingDto book(UUID lessonUuid) {
        Student student = Optional.ofNullable(authenticationUtil.getAuthenticatedStudent())
                .orElseThrow(() -> new IllegalBookingException(
                        "booking requires authentication by student"
                ));
        return bookAs(student, lessonUuid);
    }

    @Override
    public Page<BookingDto> getAllNotAcceptedYetBookings(Pageable pageable) {
        return repository.findNotAcceptedYet(pageable).map(mapper::toDto);
    }

    @Override
    public Page<BookingDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    @Transactional
    public Page<BookingDto> applyDecision(BookingDecisionDto decision, Pageable pageable) {
        Set<Booking> accepted = decision.accepted().stream()
                .map(uuid -> repository.findById(uuid).orElseThrow(
                        () -> new EntityNotFoundException("booking not found: " + uuid))
                )
                .filter(booking -> !booking.isAccepted())
                .collect(Collectors.toSet());
        accepted.forEach(booking -> booking.setAccepted(true));
        repository.saveAll(accepted);
        log.info("Decision ACCEPT applied to bookings: {}", accepted);
        Set<Booking> rejected = decision.rejected().stream()
                .map(uuid -> repository.findById(uuid).orElseThrow(
                        () -> new EntityNotFoundException("booking not found: " + uuid))
                )
                .filter(booking -> !booking.isAccepted())
                .collect(Collectors.toSet());
        repository.deleteAll(rejected);
        log.info("Decision REJECT applied to bookings: {}", rejected);
        return getAll(pageable);
    }

    private String createEmailBody(UUID uuid, ZonedDateTime startTime, boolean trial) {
        String form = "<html><body>"
                + "<h1>Booking status</h1><p>%s</p><p>%s</p><p>%s</p>"
                + "</body></html>";
        String firstParagraph = trial
                  ? """
                  Your booking has been created and is pending approval.
                  We will notify you once it has been accepted.
                  Until then, your booking is temporary confirmed but it could be changed.
                  If your booking won't be rejected, you will be able to attend the lesson.
                  """
                 : "Your booking has been confirmed.";
        String secondParagraph = "Booking ID: " + uuid;
        String thirdParagraph = "Lesson start time: " + startTime;
        return String.format(form, firstParagraph, secondParagraph, thirdParagraph);
    }
}
