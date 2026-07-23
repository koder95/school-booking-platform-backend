package pl.koder95.sbp.backend.repository;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.koder95.sbp.backend.model.Lesson;
import pl.koder95.sbp.backend.model.Student;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    @EntityGraph(attributePaths = "bookings")
    Page<Lesson> findAllByBookingsNotEmpty(Pageable pageable);

    @EntityGraph(attributePaths = {"bookings", "bookings.student"})
    Page<Lesson> findAllByBookings_Student(Student student, Pageable pageable);

    default Page<Lesson> findAllByBookingsOfStudent(Student student, Pageable pageable) {
        return findAllByBookings_Student(student, pageable);
    }
}
