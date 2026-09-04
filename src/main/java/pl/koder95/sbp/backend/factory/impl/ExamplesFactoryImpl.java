package pl.koder95.sbp.backend.factory.impl;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.koder95.sbp.backend.dto.AvailabilitySlotDto;
import pl.koder95.sbp.backend.dto.BookingDto;
import pl.koder95.sbp.backend.dto.CreateLessonRequestDto;
import pl.koder95.sbp.backend.dto.CreateStudentRequestDto;
import pl.koder95.sbp.backend.dto.CreateSubjectRequestDto;
import pl.koder95.sbp.backend.dto.CreateTeacherRequestDto;
import pl.koder95.sbp.backend.dto.LessonDto;
import pl.koder95.sbp.backend.dto.StudentDto;
import pl.koder95.sbp.backend.dto.SubjectDto;
import pl.koder95.sbp.backend.dto.TeacherDto;
import pl.koder95.sbp.backend.dto.TimeRangeDto;
import pl.koder95.sbp.backend.dto.UpdateAvailabilityRequestDto;
import pl.koder95.sbp.backend.exception.ExamplesInstallationException;
import pl.koder95.sbp.backend.factory.ExamplesFactory;
import pl.koder95.sbp.backend.service.AvailabilityService;
import pl.koder95.sbp.backend.service.AvailabilitySlotService;
import pl.koder95.sbp.backend.service.BookingService;
import pl.koder95.sbp.backend.service.LessonService;
import pl.koder95.sbp.backend.service.StudentService;
import pl.koder95.sbp.backend.service.SubjectService;
import pl.koder95.sbp.backend.service.TeacherService;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExamplesFactoryImpl implements ExamplesFactory {
    private static final TimeRangeDto WORKDAY_TIME = new TimeRangeDto(
            LocalTime.of(8, 0), LocalTime.of(16, 0)
    );
    private static final UpdateAvailabilityRequestDto AVAILABILITY
            = new UpdateAvailabilityRequestDto(
                    WORKDAY_TIME, WORKDAY_TIME, WORKDAY_TIME, WORKDAY_TIME, WORKDAY_TIME,
                    new TimeRangeDto(LocalTime.of(12, 0), LocalTime.of(13, 0))
    );
    private static int STUDENT_AI = 0;
    private static int TEACHER_AI = 0;

    private final StudentService studentService;
    private final SubjectService subjectService;
    private final TeacherService teacherService;
    private final AvailabilityService availabilityService;
    private final AvailabilitySlotService availabilitySlotService;
    private final LessonService lessonService;
    private final BookingService bookingService;

    private enum CreateDependencyStep {
        STUDENT_CREATION,
        SUBJECT_CREATION,
        TEACHER_CREATION,
        AVAILABILITY_UPDATE,
        SLOTS_GENERATION,
        LESSON_CREATION,
        BOOKING_CREATION
    }

    @Override
    public Page<BookingDto> createExamples(int step, Pageable pageable) {
        log.info("Starting create examples");
        createDependencies(step);
        return bookingService.getAll(pageable);
    }

    private synchronized void createDependencies(CreateDependencyStep step) {
        log.info("Creating dependencies for step: {}", step);
        switch (step) {
            case STUDENT_CREATION -> createStudents();
            case SUBJECT_CREATION -> createSubjects();
            case TEACHER_CREATION -> createTeachers();
            case AVAILABILITY_UPDATE -> updateAvailability();
            case SLOTS_GENERATION -> generateSlots();
            case LESSON_CREATION -> createLessons();
            case BOOKING_CREATION -> bookLesson();
            default -> throw new IllegalArgumentException("Unknown step: " + step);
        }
    }

    private void createDependencies(int step) {
        int stepIndex = step - 1;
        if (stepIndex < 0 || stepIndex >= CreateDependencyStep.values().length) {
            throw new IllegalArgumentException("Unknown step: " + step);
        }
        CreateDependencyStep currentStep = CreateDependencyStep.values()[stepIndex];
        try {
            while (currentStep != null) {
                createDependencies(currentStep);
                stepIndex++;
                if (stepIndex == CreateDependencyStep.values().length) {
                    break;
                }
                currentStep = CreateDependencyStep.values()[stepIndex];
            }
        } catch (Exception e) {
            throw new ExamplesInstallationException(
                    "Failed to install examples during step: %s[%d]"
                            .formatted(currentStep, stepIndex + 1),
                    e
            );
        }
    }

    private void bookLesson() {
        ZonedDateTime now = ZonedDateTime.now();
        LocalDate tomorrow = (
                now.getDayOfWeek() == DayOfWeek.FRIDAY ? now.plusDays(3) : now.plusDays(1)
        ).toLocalDate();
        List<LessonDto> lessons = lessonService.getAll(Pageable.unpaged()).getContent()
                .stream()
                .filter(lessonDto ->
                        tomorrow.equals(lessonDto.startTime().toLocalDate())
                )
                .filter(lessonDto -> now.isBefore(lessonDto.closingTime().minusMinutes(15)))
                .toList();
        List<StudentDto> students = studentService.getAll(Pageable.unpaged()).getContent();
        for (LessonDto lesson : lessons) {
            for (StudentDto student : students) {
                bookingService.book(lesson.uuid(), student.uuid());
            }
        }
        log.info("Created bookings: {}", bookingService.getAll(Pageable.unpaged()).getContent());
    }

    private void createLessons() {
        List<TeacherDto> teachers = teacherService.getAll(Pageable.unpaged()).getContent();
        for (TeacherDto teacher : teachers) {
            List<AvailabilitySlotDto> slots = availabilitySlotService
                    .getAllFor(teacher.uuid(), Pageable.unpaged())
                    .getContent();
            for (AvailabilitySlotDto slot : slots) {
                lessonService.create(new CreateLessonRequestDto(
                        slot.uuid(),
                        teacher.uuid(),
                        BigInteger.valueOf(studentService.count()).intValueExact()
                ));
            }
        }
        log.info("Created lessons: {}", lessonService.getAll(Pageable.unpaged()).getContent());
    }

    private void generateSlots() {
        Page<AvailabilitySlotDto> all = availabilitySlotService.createOrGetAll(Pageable.unpaged());
        log.info("Generated availability slots: {}", all.getContent());
    }

    private void updateAvailability() {
        List<TeacherDto> teachers = teacherService.getAll(Pageable.unpaged()).getContent();
        for (TeacherDto teacher : teachers) {
            availabilityService.updateFor(teacher.uuid(), AVAILABILITY);
        }
        log.info("Updated availability for teachers: {}", teachers);
    }

    private void createTeachers() {
        List<SubjectDto> subjects = subjectService.getAll(Pageable.unpaged()).getContent();
        List<TeacherDto> created = new java.util.ArrayList<>();
        for (SubjectDto subject : subjects) {
            int i = TEACHER_AI++;
            String email = subject.name().toLowerCase().replaceAll("\\s", ".");
            created.add(teacherService.create(new CreateTeacherRequestDto(
                    "teacher%d.%s@example.com".formatted(i, email),
                    subject.id(),
                    "Name %d".formatted(i),
                    "Somebody",
                    randomZoneId(),
                    null, null, null)
            ));
        }
        log.info("Created teachers: {}", created);
    }

    private void createSubjects() {
        createSubjects(List.of(
                "Chemistry", "Ukrainian", "Physics", "Biology", "History",
                "Literature", "English", "Mathematics", "Polish"
        ));
    }

    private void createSubjects(List<String> names) {
        log.info("Created subjects: {}", names.stream().map(this::createSubject).toList());
    }

    private SubjectDto createSubject(String name) {
        return subjectService.create(new CreateSubjectRequestDto(name, null));
    }

    private void createStudents() {
        List<StudentDto> created = new java.util.ArrayList<>();
        for (int i = 0; i < 10; i++) {
            created.add(studentService.create(new CreateStudentRequestDto(
                    "student%d@example.com".formatted(STUDENT_AI++),
                    randomZoneId(),
                    i > 5
            )));
        }
        log.info("Created students: {}", created);
    }

    private static ZoneId randomZoneId() {
        String id = ZoneId.getAvailableZoneIds().stream()
                .toList()
                .get((int) (Math.random() * ZoneId.getAvailableZoneIds().size()));
        return ZoneId.of(id);
    }
}
