package pl.koder95.sbp.backend.specification.impl;

import jakarta.persistence.criteria.Expression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import pl.koder95.sbp.backend.dto.LessonSearchParamsDto;
import pl.koder95.sbp.backend.dto.SubjectDto;
import pl.koder95.sbp.backend.model.Lesson;
import pl.koder95.sbp.backend.specification.FilterStrategy;

@Component
@RequiredArgsConstructor
public class LessonSubjectFilterStrategy implements FilterStrategy<Lesson, LessonSearchParamsDto> {
    @Override
    public boolean isApplicable(LessonSearchParamsDto criteria) {
        return criteria.subject() != null;
    }

    @Override
    public Specification<Lesson> compile(LessonSearchParamsDto criteria) {
        SubjectDto subject = criteria.subject();
        if (subject == null) {
            return Specification.unrestricted();
        }
        if (subject.id() != null) {
            return forId(subject);
        }
        Specification<Lesson> forName = subject.name() == null
                ? Specification.unrestricted() : forName(subject);
        Specification<Lesson> forDescription = subject.description() == null
                ? Specification.unrestricted() : forDescription(subject);
        return forName.and(forDescription);
    }

    private Specification<Lesson> forId(SubjectDto subject) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("subject.id"), subject.id());
    }

    private Specification<Lesson> forName(SubjectDto subject) {
        return (root, query, criteriaBuilder)
                -> {
            Expression<String> lower = criteriaBuilder.lower(root.get("subject.name"));
            return criteriaBuilder.like(lower, "%" + subject.name().toLowerCase() + "%");
        };
    }

    private Specification<Lesson> forDescription(SubjectDto subject) {
        return (root, query, criteriaBuilder)
                -> {
            Expression<String> lower = criteriaBuilder.lower(root.get("subject.description"));
            return criteriaBuilder.like(lower, "%" + subject.description().toLowerCase() + "%");
        };
    }
}
