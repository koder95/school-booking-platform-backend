package pl.koder95.sbp.backend.specification;

import org.springframework.data.jpa.domain.Specification;

public interface FilterStrategy<T, C> {
    boolean isApplicable(C criteria);

    Specification<T> compile(C criteria);
}
