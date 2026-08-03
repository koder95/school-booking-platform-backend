package pl.koder95.sbp.backend.service;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.koder95.sbp.backend.dto.BookingDecisionDto;
import pl.koder95.sbp.backend.dto.BookingDto;

public interface BookingService {
    BookingDto book(UUID lessonUuid);

    Page<BookingDto> getAllNotAcceptedYetBookings(Pageable pageable);

    Page<BookingDto> getAll(Pageable pageable);

    Page<BookingDto> applyDecision(BookingDecisionDto decision, Pageable pageable);
}
