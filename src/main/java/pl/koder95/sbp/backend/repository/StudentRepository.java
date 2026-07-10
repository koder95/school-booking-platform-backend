package pl.koder95.sbp.backend.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.koder95.sbp.backend.model.Email;
import pl.koder95.sbp.backend.model.Student;

public interface StudentRepository extends JpaRepository<Student, UUID> {
    Optional<Student> findByEmail(Email email);
}
