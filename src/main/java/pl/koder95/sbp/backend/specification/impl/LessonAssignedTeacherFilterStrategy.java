package pl.koder95.sbp.backend.specification.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import pl.koder95.sbp.backend.dto.LessonSearchParamsDto;
import pl.koder95.sbp.backend.model.Lesson;
import pl.koder95.sbp.backend.specification.FilterStrategy;

@Component
@RequiredArgsConstructor
public class LessonAssignedTeacherFilterStrategy
        implements FilterStrategy<Lesson, LessonSearchParamsDto> {
    @Override
    public boolean isApplicable(LessonSearchParamsDto criteria) {
        return criteria.assignedTeacherUuid() != null;
    }

    @Override
    public Specification<Lesson> compile(LessonSearchParamsDto criteria) {
        UUID assigned = criteria.assignedTeacherUuid();
        if (assigned == null) {
            return Specification.unrestricted();
        }
        return forUuid(assigned);
    }

    private Specification<Lesson> forUuid(UUID assignedTeacherUuid) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("assigned").get("uuid"), assignedTeacherUuid);
    }
}
