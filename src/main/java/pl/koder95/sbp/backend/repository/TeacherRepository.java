package pl.koder95.sbp.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.koder95.sbp.backend.model.AvailabilitySlot;
import pl.koder95.sbp.backend.model.Subject;
import pl.koder95.sbp.backend.model.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, UUID> {
    @Query(value = "from Teacher t where :slot member of t.availabilitySlots")
    List<Teacher> findByAvailabilitySlot(AvailabilitySlot slot);

    @Query("SELECT t FROM Teacher t "
            + "LEFT JOIN Lesson l ON l.assigned = t "
            + "WHERE t IN :teachers "
            + "GROUP BY t "
            + "ORDER BY COUNT(l) ASC")
    List<Teacher> findTeachersOrderedByLessonCountAsc(Set<Teacher> teachers, Pageable pageable);

    @Query("SELECT t FROM Teacher t "
            + "LEFT JOIN Lesson l ON l.assigned = t "
            + "WHERE t IN :teachers AND t.subject = :subject "
            + "GROUP BY t "
            + "ORDER BY COUNT(l) ASC")
    List<Teacher> findTeachersOrderedByLessonCountAsc(
            Set<Teacher> teachers, Subject subject, Pageable pageable
    );

    default Optional<Teacher> findTeacherWithFewestLessonsAmong(Set<Teacher> teachers) {
        List<Teacher> list = findTeachersOrderedByLessonCountAsc(teachers, PageRequest.of(0, 1));
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    default Optional<Teacher> findTeacherWithFewestLessonsAmong(Set<Teacher> teachers,
                                                                Subject subject) {
        List<Teacher> list = findTeachersOrderedByLessonCountAsc(teachers, subject,
                PageRequest.of(0, 1)
        );
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }
}
