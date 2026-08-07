package pl.koder95.sbp.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "lessons")
@SQLDelete(sql = "UPDATE lessons SET is_deleted = true WHERE uuid = ?")
@SQLRestriction("is_deleted = false")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Subject subject;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Teacher assigned;
    @Column(nullable = false)
    private ZonedDateTime createdAt;
    @Column(nullable = false)
    private ZonedDateTime startTime;
    @Column(nullable = false)
    private ZonedDateTime closingTime;
    @Column(nullable = false)
    private int maxEnrolled;
    @OneToMany(mappedBy = "lesson")
    private Set<Booking> bookings;
    private boolean isDeleted;
}
