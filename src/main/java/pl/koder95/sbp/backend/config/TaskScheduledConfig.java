package pl.koder95.sbp.backend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import pl.koder95.sbp.backend.service.AvailabilitySlotService;
import pl.koder95.sbp.backend.service.LessonService;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class TaskScheduledConfig {
    private final AvailabilitySlotService availabilitySlotService;
    private final LessonService lessonService;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanAvailabilitySlots() {
        availabilitySlotService.cleanOldAvailabilitySlots();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void generateLessons() {
        log.info("Generating lessons...");
        lessonService.generateFromAllAvailableSlots(Pageable.unpaged())
                .forEach(lessonDto -> log.info(
                        "Created lession {}, assigned: {}",
                        lessonDto.uuid(), lessonDto.teacherUuid()
                ));
    }
}
