package pl.koder95.sbp.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "students_abilities")
public class Abilities {
    @Id
    private UUID studentUuid;
    @MapsId
    @OneToOne
    private Student student;
    private int remainingLessons = 0;
}
