package pl.koder95.sbp.backend.config;

import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import pl.koder95.sbp.backend.repository.AvailabilitySlotRepository;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class TaskScheduledConfig {
    private final AvailabilitySlotRepository availabilitySlotRepository;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanAvailabilitySlots() {
        availabilitySlotRepository.deleteByTimestampBefore(ZonedDateTime.now());
    }
}
