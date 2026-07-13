package pl.koder95.sbp.backend.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.koder95.sbp.backend.model.Student;

public interface StudentRepository extends JpaRepository<Student, UUID> {
    @Query("FROM Student s JOIN FETCH s.email WHERE s.email.value = :email")
    Optional<Student> findByEmail(String email);
}
