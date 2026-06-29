package pl.koder95.sbp.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "availability_slots")
@SQLDelete(sql = "UPDATE availability_slots SET is_deleted = true WHERE uuid = ?")
@SQLRestriction("is_deleted = false")
public class AvailabilitySlot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
    @ManyToMany
    @JoinTable(
            name = "teachers_availability_slots",
            joinColumns = @JoinColumn(name = "availability_slot_uuid"),
            inverseJoinColumns = @JoinColumn(name = "teacher_uuid")
    )
    private Set<Teacher> teachers;
    @Column(nullable = false, unique = true)
    private ZonedDateTime timestamp;
    private boolean isDeleted;

    public AvailabilitySlot addTeacher(Teacher teacher) {
        if (teachers == null) {
            teachers = new java.util.HashSet<>();
        }
        teachers.add(teacher);
        return this;
    }

    public AvailabilitySlot removeTeacher(Teacher teacher) {
        if (teachers != null) {
            teachers.remove(teacher);
        }
        return this;
    }
}
