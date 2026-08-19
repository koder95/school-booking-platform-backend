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
public class LessonStillOpenFilterStrategy
        implements FilterStrategy<Lesson, LessonSearchParamsDto> {
    @Override
    public boolean isApplicable(LessonSearchParamsDto criteria) {
        return criteria.open() != null;
    }

    @Override
    public Specification<Lesson> compile(LessonSearchParamsDto criteria) {
        return (root, query, criteriaBuilder) -> {
            Path<Instant> closeTime = root.get("closingTime");
            Instant now = Instant.now();
            return criteria.open()
                    ? criteriaBuilder.lessThan(closeTime, now)
                    : criteriaBuilder.greaterThanOrEqualTo(closeTime, now);
        };
    }
}
