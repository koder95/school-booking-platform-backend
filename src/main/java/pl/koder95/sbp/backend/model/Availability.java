package pl.koder95.sbp.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(name = "availabilities")
@SQLDelete(sql = "UPDATE availabilities SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Availability {
    @Id
    private UUID uuid;
    @MapsId
    @OneToOne
    @JoinColumn(nullable = false)
    private Teacher teacher;
    @ManyToOne
    private TimeRange mondayRange;
    @ManyToOne
    private TimeRange tuesdayRange;
    @ManyToOne
    private TimeRange wednesdayRange;
    @ManyToOne
    private TimeRange thursdayRange;
    @ManyToOne
    private TimeRange fridayRange;
    @ManyToOne
    private TimeRange lunchBreakRange;
    private boolean isDeleted;
}
