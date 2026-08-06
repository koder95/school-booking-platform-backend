package pl.koder95.sbp.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import pl.koder95.sbp.backend.service.AvailabilitySlotService;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class TaskScheduledConfig {
    private final AvailabilitySlotService availabilitySlotService;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanAvailabilitySlots() {
        availabilitySlotService.cleanOldAvailabilitySlots();
    }
}
