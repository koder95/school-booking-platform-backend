package pl.koder95.sbp.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.koder95.sbp.backend.model.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByName(String name);

    @Query("SELECT s FROM Subject s "
            + "LEFT JOIN Lesson l ON l.subject = s "
            + "WHERE s IN :subjects "
            + "GROUP BY s "
            + "ORDER BY COUNT(l) ASC")
    List<Subject> findSubjectsOrderedByLessonCountAsc(Set<Subject> subjects, Pageable pageable);

    default Optional<Subject> findSubjectWithFewestLessonAmong(Set<Subject> subjects) {
        List<Subject> list = findSubjectsOrderedByLessonCountAsc(subjects, PageRequest.of(0, 1));
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }
}
