package pl.koder95.sbp.backend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import pl.koder95.sbp.backend.service.AvailabilitySlotService;
import pl.koder95.sbp.backend.service.EmailDeliveryService;
import pl.koder95.sbp.backend.service.LessonService;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class TaskScheduledConfig {
    private final AvailabilitySlotService availabilitySlotService;
    private final LessonService lessonService;
    private final EmailDeliveryService emailDeliveryService;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanAvailabilitySlots() {
        log.info("Removing old availability slots...");
        availabilitySlotService.cleanOldAvailabilitySlots();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void generateLessons() {
        log.info("Generating available slots...");
        long slotsCount = availabilitySlotService.createOrGetAll(Pageable.unpaged())
                .getTotalElements();
        log.info("Generated slots: {}", slotsCount);
        if (slotsCount != 0) {
            lessonService.generateFromAllAvailableSlots(Pageable.unpaged());
        }
    }

    @Scheduled(cron = "*/5 * * * * *")
    public void sendEmails() {
        log.info("Checking email delivery requests...");
        emailDeliveryService.sendAll();
    }
}
