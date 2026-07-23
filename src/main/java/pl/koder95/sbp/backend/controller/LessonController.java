package pl.koder95.sbp.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.koder95.sbp.backend.dto.BookingDto;
import pl.koder95.sbp.backend.dto.CreateLessonRequestDto;
import pl.koder95.sbp.backend.dto.LessonDto;
import pl.koder95.sbp.backend.dto.UpdateLessonRequestDto;
import pl.koder95.sbp.backend.service.BookingService;
import pl.koder95.sbp.backend.service.LessonService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lessons")
@Tag(name = "Lesson management", description = "The endpoint to manage lessons")
public class LessonController {
    private final LessonService lessonService;
    private final BookingService bookingService;

    @GetMapping
    @Operation(summary = "Get all lessons", description = "Get all lessons with pagination")
    public Page<LessonDto> getAll(@ParameterObject Pageable pageable) {
        return lessonService.findAll(pageable);
    }

    @GetMapping("/{lessonUuid}")
    @Operation(summary = "Get lesson by UUID", description = "Get a specific lesson by its UUID")
    public LessonDto getByUuid(@PathVariable UUID lessonUuid) {
        return lessonService.getByUuid(lessonUuid);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Create lesson", description = "Create a new lesson")
    public LessonDto create(@RequestBody CreateLessonRequestDto requestDto) {
        return lessonService.create(requestDto);
    }

    @PostMapping("/{lessonUuid}/booking")
    @PreAuthorize("hasRole('STUDENT')")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Book lesson", description = "Book a lesson as student")
    public BookingDto book(@PathVariable UUID lessonUuid) {
        return bookingService.book(lessonUuid);
    }

    @PutMapping("/{lessonUuid}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Update lesson", description = "Update an existing lesson")
    public LessonDto update(@PathVariable UUID lessonUuid,
                            @RequestBody UpdateLessonRequestDto requestDto) {
        return lessonService.update(lessonUuid, requestDto);
    }

    @DeleteMapping("/{lessonUuid}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Delete lesson", description = "Delete an existing lesson")
    public LessonDto delete(@PathVariable UUID lessonUuid) {
        return lessonService.deleteById(lessonUuid);
    }

    @GetMapping("/booked")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Get all booked lessons",
            description = """
                    Get all booked lesson with pagination.
                    If you use this as a user with admin role,
                    it returns a booked lessons for all students.
                    If you use this as a user with student role,
                    it returns a booked lessons for your account.
                    """)
    public Page<LessonDto> getBooked(@ParameterObject Pageable pageable) {
        return lessonService.findAllBooked(pageable);
    }
}
