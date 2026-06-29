package pl.koder95.sbp.backend.repository;

import java.time.Period;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.koder95.sbp.backend.model.AvailabilitySlot;
import pl.koder95.sbp.backend.model.Teacher;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, UUID> {
    @Query(
            "select a from AvailabilitySlot a inner join a.teachers t "
                    + "where t = ?1 and a.timestamp = ?2"
    )
    Optional<AvailabilitySlot> findByTeacherAndTimestamp(Teacher teacher, ZonedDateTime timestamp);

    @Query(
            "select a from AvailabilitySlot a inner join a.teachers t where t = ?1"
    )
    List<AvailabilitySlot> findAllByTeacher(Teacher teacher);

    @Query(
            "select a from AvailabilitySlot a inner join a.teachers t where t = ?1"
    )
    Page<AvailabilitySlot> findAllByTeacher(Teacher teacher, Pageable pageable);

    List<AvailabilitySlot> findAllByTimestampBetween(
            ZonedDateTime start, ZonedDateTime end
    );

    Page<AvailabilitySlot> findAllByTimestampBetween(
            ZonedDateTime start, ZonedDateTime end, Pageable pageable
    );

    @Query(
            "select a from AvailabilitySlot a inner join a.teachers t where t = ?1 "
                    + "and a.timestamp between ?2 and ?3"
    )
    List<AvailabilitySlot> findAllByTeacherAndTimestampBetween(
            Teacher teacher, ZonedDateTime start, ZonedDateTime end
    );

    @Query(
            "select a from AvailabilitySlot a inner join a.teachers t where t = ?1 "
                    + "and a.timestamp between ?2 and ?3"
    )
    Page<AvailabilitySlot> findAllByTeacherAndTimestampBetween(
            Teacher teacher, ZonedDateTime start, ZonedDateTime end, Pageable pageable
    );

    default List<AvailabilitySlot> findAll(Teacher teacher, Period period) {
        ZonedDateTime startTime = ZonedDateTime.now();
        return findAllByTeacherAndTimestampBetween(teacher, startTime, startTime.plus(period));
    }

    boolean existsByTimestamp(ZonedDateTime timestamp);

    Optional<AvailabilitySlot> findByTimestamp(ZonedDateTime timestamp);
}
