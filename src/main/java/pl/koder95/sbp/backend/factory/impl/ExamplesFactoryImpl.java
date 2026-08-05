package pl.koder95.sbp.backend.factory.impl;

import java.math.BigInteger;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.koder95.sbp.backend.dto.AvailabilitySlotDto;
import pl.koder95.sbp.backend.dto.BookingDto;
import pl.koder95.sbp.backend.dto.CreateLessonRequestDto;
import pl.koder95.sbp.backend.dto.CreateStudentRequestDto;
import pl.koder95.sbp.backend.dto.CreateSubjectRequestDto;
import pl.koder95.sbp.backend.dto.CreateTeacherRequestDto;
import pl.koder95.sbp.backend.dto.LessonDto;
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
public class ExamplesFactoryImpl implements ExamplesFactory {
    private static final UpdateAvailabilityRequestDto AVAILABILITY
            = new UpdateAvailabilityRequestDto(
                    new TimeRangeDto(LocalTime.of(8, 0), LocalTime.of(16, 0)),
                    new TimeRangeDto(LocalTime.of(8, 0), LocalTime.of(16, 0)),
                    new TimeRangeDto(LocalTime.of(8, 0), LocalTime.of(16, 0)),
                    new TimeRangeDto(LocalTime.of(8, 0), LocalTime.of(16, 0)),
                    new TimeRangeDto(LocalTime.of(8, 0), LocalTime.of(16, 0)),
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

    @Override
    @Transactional
    public Page<BookingDto> createExamples(Pageable pageable) {
        createDependencies();
        return bookingService.getAll(pageable);
    }

    private synchronized void createDependencies() {
        int step = 1;
        try {
            createStudents();
            step++;
            createSubjects();
            step++;
            createTeachers();
            step++;
            updateAvailability();
            step++;
            generateSlots();
            step++;
            createLessons();
            step++;
            bookLesson();
        } catch (Exception e) {
            throw new ExamplesInstallationException(
                    "Failed to install examples during step " + step
            );
        }
    }

    private void bookLesson() {
        List<LessonDto> lessons = lessonService.getAll(Pageable.unpaged()).getContent();
        for (LessonDto lesson : lessons) {
            bookingService.book(lesson.uuid());
        }
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
    }

    private void generateSlots() {
        availabilitySlotService.createOrGetAll(Pageable.unpaged());
    }

    private void updateAvailability() {
        List<TeacherDto> teachers = teacherService.getAll(Pageable.unpaged()).getContent();
        for (TeacherDto teacher : teachers) {
            availabilityService.updateFor(teacher.uuid(), AVAILABILITY);
        }
    }

    private void createTeachers() {
        List<SubjectDto> subjects = subjectService.getAll(Pageable.unpaged()).getContent();
        for (SubjectDto subject : subjects) {
            int i = TEACHER_AI++;
            teacherService.create(new CreateTeacherRequestDto(
                    "teacher%d.%s@example.com".formatted(i, subject.name()),
                    subject.id(),
                    "Name %d".formatted(i),
                    "Somebody",
                    randomZoneId())
            );
        }
    }

    private void createSubjects() {
        subjectService.create(new CreateSubjectRequestDto("Math", null));
        subjectService.create(new CreateSubjectRequestDto("Science", null));
        subjectService.create(new CreateSubjectRequestDto("History", null));
        subjectService.create(new CreateSubjectRequestDto("Geography", null));
        subjectService.create(new CreateSubjectRequestDto("English", null));
        subjectService.create(new CreateSubjectRequestDto("Art", null));
        subjectService.create(new CreateSubjectRequestDto("Music", null));
        subjectService.create(new CreateSubjectRequestDto("Physical Education", null));
    }

    private void createStudents() {
        for (int i = 0; i < 10; i++) {
            studentService.create(new CreateStudentRequestDto(
                    "student%d@example.com".formatted(STUDENT_AI++),
                    randomZoneId(),
                    i > 5
            ));
        }
    }

    private static ZoneId randomZoneId() {
        String id = ZoneId.getAvailableZoneIds().stream()
                .toList()
                .get((int) (Math.random() * ZoneId.getAvailableZoneIds().size()));
        return ZoneId.of(id);
    }
}
