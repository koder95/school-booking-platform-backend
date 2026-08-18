package pl.koder95.sbp.backend.specification.impl;

import jakarta.persistence.criteria.Path;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import pl.koder95.sbp.backend.dto.LessonSearchParamsDto;
import pl.koder95.sbp.backend.model.Lesson;
import pl.koder95.sbp.backend.specification.FilterStrategy;

@Component
@RequiredArgsConstructor
public class LessonFromToFilterStrategy implements FilterStrategy<Lesson, LessonSearchParamsDto> {
    @Override
    public boolean isApplicable(LessonSearchParamsDto criteria) {
        return criteria.from() != null || criteria.to() != null;
    }

    @Override
    public Specification<Lesson> compile(LessonSearchParamsDto criteria) {
        return (root, query, criteriaBuilder) -> {
            Path<Instant> startTime = root.get("startTime");
            if (criteria.from() == null) {
                return criteriaBuilder.between(startTime, Instant.now(), criteria.to());
            }
            if (criteria.to() == null) {
                return criteriaBuilder.greaterThanOrEqualTo(startTime, criteria.from());
            }
            return criteriaBuilder.between(startTime, criteria.from(), criteria.to());
        };
    }
}
