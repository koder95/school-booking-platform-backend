package pl.koder95.sbp.backend.factory.impl;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;
import pl.koder95.sbp.backend.factory.LocalTimeFactory;

@Component
public class FullHoursLocalTimeFactory implements LocalTimeFactory {
    @Override
    public List<LocalTime> createTimestampsBetween(LocalTime start, LocalTime end) {
        start = normalizeStartTime(start);
        if (end == null || !start.isBefore(end)) {
            return Collections.emptyList();
        }
        List<LocalTime> list = new ArrayList<>();
        while (!start.isAfter(end)) {
            list.add(start);
            start = start.plusHours(1);
        }
        List<LocalTime> result = List.copyOf(list);
        list.clear();
        return result;
    }

    private static LocalTime normalizeStartTime(LocalTime time) {
        if (time == null) {
            return normalizeStartTime(LocalTime.now());
        }
        LocalTime fullHourTime = time.withMinute(0).withSecond(0).withNano(0);
        if (time.equals(fullHourTime)) {
            return time;
        }
        return fullHourTime.plusHours(1);
    }
}
