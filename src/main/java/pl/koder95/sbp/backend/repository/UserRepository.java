package pl.koder95.sbp.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.koder95.sbp.backend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("FROM User u JOIN FETCH u.email WHERE u.email.value = :email")
    Optional<User> findByEmail(String email);
}
