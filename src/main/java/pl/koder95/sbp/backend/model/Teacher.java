package pl.koder95.sbp.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.ZoneId;
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
@Table(name = "teachers")
@SQLDelete(sql = "UPDATE teachers SET is_deleted = true WHERE uuid = ?")
@SQLRestriction("is_deleted = false")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
    @OneToOne
    @JoinColumn(unique = true, nullable = false)
    private Email email;
    private String firstName;
    private String lastName;
    @OneToOne(mappedBy = "teacher")
    private Availability availability;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Subject subject;
    @ManyToMany(mappedBy = "teachers")
    private Set<AvailabilitySlot> availabilitySlots;
    @Column(nullable = false, length = 64)
    private ZoneId zoneId = ZoneId.systemDefault();
    private boolean isDeleted;
}
