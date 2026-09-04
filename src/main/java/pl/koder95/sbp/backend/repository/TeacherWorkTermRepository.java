package pl.koder95.sbp.backend.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.koder95.sbp.backend.model.Teacher;
import pl.koder95.sbp.backend.model.TeacherWorkTerm;

public interface TeacherWorkTermRepository extends JpaRepository<TeacherWorkTerm, UUID> {
    TeacherWorkTerm findByTeacher(Teacher teacher);
}
