package pl.koder95.sbp.backend.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T, C> {
    List<FilterStrategy<T, C>> getFilterStrategies();

    default Specification<T> build(C criteria) {
        return getFilterStrategies().stream()
                .filter(strategy -> strategy.isApplicable(criteria))
                .map(strategy -> strategy.compile(criteria))
                .reduce(Specification.unrestricted(), Specification::and);
    }
}
