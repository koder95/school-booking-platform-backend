package pl.koder95.sbp.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "time_ranges", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"start_time", "end_time"}, name = "u_times")
})
@SQLDelete(sql = "UPDATE time_ranges SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class TimeRange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private LocalTime startTime;
    @Column(nullable = false, unique = true)
    private LocalTime endTime;
    private boolean isDeleted;
}
