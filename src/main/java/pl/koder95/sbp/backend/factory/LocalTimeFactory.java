package pl.koder95.sbp.backend.factory;

import java.time.LocalTime;
import java.util.List;

public interface LocalTimeFactory {
    List<LocalTime> createTimestampsBetween(LocalTime start, LocalTime end);
}
