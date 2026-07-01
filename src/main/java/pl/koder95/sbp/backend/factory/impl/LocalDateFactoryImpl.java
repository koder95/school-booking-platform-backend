package pl.koder95.sbp.backend.factory.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import pl.koder95.sbp.backend.factory.LocalDateFactory;

@Component
public class LocalDateFactoryImpl implements LocalDateFactory {
    @Override
    public List<LocalDate> createDatesForPeriod(LocalDate start, LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        while (!start.isAfter(end)) {
            dates.add(start);
            start = start.plusDays(1);
        }
        return List.copyOf(dates);
    }
}
