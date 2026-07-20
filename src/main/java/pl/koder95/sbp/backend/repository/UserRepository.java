package pl.koder95.sbp.backend.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.koder95.sbp.backend.model.Authority;
import pl.koder95.sbp.backend.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("FROM User u JOIN FETCH u.email WHERE u.email.value = :email")
    Optional<User> findByEmail(String email);

    Page<User> findAllByAuthority(Authority authority, Pageable pageable);

    boolean existsByAuthority(Authority authority);
}
