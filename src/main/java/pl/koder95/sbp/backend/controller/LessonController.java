package pl.koder95.sbp.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.koder95.sbp.backend.dto.CreateLessonRequestDto;
import pl.koder95.sbp.backend.dto.LessonDto;
import pl.koder95.sbp.backend.dto.UpdateLessonRequestDto;
import pl.koder95.sbp.backend.service.AvailabilitySlotService;
import pl.koder95.sbp.backend.service.LessonService;
import pl.koder95.sbp.backend.service.TeacherService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lessons")
@Tag(name = "Lesson management", description = "The endpoint to manage lessons")
public class LessonController {
    private final LessonService lessonService;
    private final TeacherService teacherService;
    private final AvailabilitySlotService availabilitySlotService;

    @GetMapping
    public Page<LessonDto> getAll(Pageable pageable) {
        return lessonService.findAll(pageable);
    }

    @GetMapping("/{lessonUuid}")
    public LessonDto getByUuid(@PathVariable UUID lessonUuid) {
        return lessonService.getByUuid(lessonUuid);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public LessonDto create(@RequestBody CreateLessonRequestDto requestDto) {
        return lessonService.create(requestDto);
    }

    @PutMapping("/{lessonUuid}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public LessonDto update(@PathVariable UUID lessonUuid,
                            @RequestBody UpdateLessonRequestDto requestDto) {
        return lessonService.update(lessonUuid, requestDto);
    }

    @DeleteMapping("/{lessonUuid}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public LessonDto delete(@PathVariable UUID lessonUuid) {
        return lessonService.deleteById(lessonUuid);
    }
}
