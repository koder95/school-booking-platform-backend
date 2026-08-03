package pl.koder95.sbp.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "bookings", uniqueConstraints = {
        @UniqueConstraint(name = "uq_student_lesson_uuids", columnNames = {
                "student_uuid", "lesson_uuid"
        })
})
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
    @JoinColumn(name = "student_uuid", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Student student;
    @JoinColumn(name = "lesson_uuid", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Lesson lesson;
    @Column(nullable = false)
    private ZonedDateTime bookedAt;
    @Column(nullable = false)
    private boolean isAccepted = false;

    public Booking(Student student, Lesson lesson) {
        this.student = student;
        this.lesson = lesson;
        this.bookedAt = ZonedDateTime.now(student.getZoneId());
        this.isAccepted = !student.isTrial();
    }
}
