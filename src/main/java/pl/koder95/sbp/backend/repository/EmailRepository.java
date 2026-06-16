package pl.koder95.sbp.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.koder95.sbp.backend.model.Email;

public interface EmailRepository extends JpaRepository<Email, Long> {
    boolean existsByValue(String value);

    Optional<Email> findByValue(String value);
}
