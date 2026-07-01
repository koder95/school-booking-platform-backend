package pl.koder95.sbp.backend.factory;

import java.time.LocalDate;
import java.util.List;

public interface LocalDateFactory {
    List<LocalDate> createDatesForPeriod(LocalDate start, LocalDate end);
}
