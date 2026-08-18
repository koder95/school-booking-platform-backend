package pl.koder95.sbp.backend.specification;

import pl.koder95.sbp.backend.dto.LessonSearchParamsDto;
import pl.koder95.sbp.backend.model.Lesson;

public final class LessonSpecification {

    public interface Builder extends SpecificationBuilder<Lesson, LessonSearchParamsDto> {
    }
}
