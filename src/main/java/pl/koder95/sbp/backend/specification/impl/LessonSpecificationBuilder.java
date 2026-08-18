package pl.koder95.sbp.backend.specification.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.koder95.sbp.backend.dto.LessonSearchParamsDto;
import pl.koder95.sbp.backend.model.Lesson;
import pl.koder95.sbp.backend.specification.FilterStrategy;
import pl.koder95.sbp.backend.specification.LessonSpecification;

@Component
@RequiredArgsConstructor
public class LessonSpecificationBuilder implements LessonSpecification.Builder {
    private final List<FilterStrategy<Lesson, LessonSearchParamsDto>> filterStrategies;

    @Override
    public List<FilterStrategy<Lesson, LessonSearchParamsDto>> getFilterStrategies() {
        return filterStrategies;
    }
}
