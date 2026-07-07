package pl.koder95.sbp.backend.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.koder95.sbp.backend.model.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {
}
