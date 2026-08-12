package pl.koder95.sbp.backend.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.koder95.sbp.backend.model.AvailabilitySlot;
import pl.koder95.sbp.backend.model.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, UUID> {
    @Query(value = "from Teacher t where :slot member of t.availabilitySlots")
    List<Teacher> findByAvailabilitySlot(AvailabilitySlot slot);
}
