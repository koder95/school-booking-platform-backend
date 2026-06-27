package pl.koder95.sbp.backend.repository;

import java.time.LocalTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.koder95.sbp.backend.model.TimeRange;

public interface TimeRangeRepository extends JpaRepository<TimeRange, Long> {
    Optional<TimeRange> findFirstByStartTimeAndEndTime(LocalTime startTime, LocalTime endTime);

    default Optional<TimeRange> findBy(LocalTime startTime, LocalTime endTime) {
        return findFirstByStartTimeAndEndTime(startTime, endTime);
    }
}
