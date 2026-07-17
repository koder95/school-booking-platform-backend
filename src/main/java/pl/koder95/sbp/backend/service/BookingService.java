package pl.koder95.sbp.backend.service;

import java.util.UUID;
import pl.koder95.sbp.backend.dto.BookingDto;

public interface BookingService {
    BookingDto book(UUID lessonUuid);
}
