package pl.koder95.sbp.backend.repository;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.koder95.sbp.backend.model.Booking;
import pl.koder95.sbp.backend.model.Lesson;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    int countDistinctByLesson(Lesson lesson);

    Page<Booking> findByIsAccepted(boolean isAccepted, Pageable pageable);

    default Page<Booking> findNotAcceptedYet(Pageable pageable) {
        return findByIsAccepted(false, pageable);
    }
}
