package pl.koder95.sbp.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.koder95.sbp.backend.model.DeliveryStatus;
import pl.koder95.sbp.backend.model.EmailDeliveryLog;

public interface EmailDeliveryLogRepository extends JpaRepository<EmailDeliveryLog, Long> {
    List<EmailDeliveryLog> findByStatus(DeliveryStatus status);
}
