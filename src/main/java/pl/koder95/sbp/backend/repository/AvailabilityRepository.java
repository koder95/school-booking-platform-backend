package pl.koder95.sbp.backend.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.koder95.sbp.backend.model.Availability;

public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {
}
