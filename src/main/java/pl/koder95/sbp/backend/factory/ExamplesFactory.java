package pl.koder95.sbp.backend.factory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.koder95.sbp.backend.dto.BookingDto;

public interface ExamplesFactory {
    Page<BookingDto> createExamples(Pageable pageable);
}
