package pl.koder95.sbp.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "teachers_work_terms")
public class TeacherWorkTerm {
    @Id
    private UUID uuid;
    @MapsId
    @OneToOne
    @JoinColumn(name = "teacher_uuid")
    private Teacher teacher;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkType workType = WorkType.FREELANCE;
    private LocalDate workDueDate;
}
