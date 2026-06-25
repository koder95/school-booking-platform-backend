package pl.koder95.sbp.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import pl.koder95.sbp.backend.dto.CreateTeacherRequestDto;
import pl.koder95.sbp.backend.dto.TeacherDto;
import pl.koder95.sbp.backend.dto.UpdateTeacherRequestDto;
import pl.koder95.sbp.backend.service.TeacherService;

@SecurityRequirement(name = "bearer-key")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teachers")
@Tag(name = "Teacher management", description = "The endpoint to manage teachers")
public class TeacherController {
    private final TeacherService teacherService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(
            summary = "Get all teachers",
            description = "Retrieve a paginated list of teachers. "
                    + "Only available for users with ADMIN role."
    )
    public Page<TeacherDto> getAll(@ParameterObject Pageable pageable) {
        return teacherService.getAll(pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/{uuid}")
    @Operation(
            summary = "Get teacher by UUID",
            description = "Retrieve a specific teacher by their UUID. "
                    + "Available for users with ADMIN or STUDENT role."
    )
    public TeacherDto get(@PathVariable UUID uuid) {
        return teacherService.get(uuid);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(
            summary = "Create a new teacher",
            description = "Create a new teacher. Only available for users with ADMIN role."
    )
    public TeacherDto create(@Valid @RequestBody CreateTeacherRequestDto dto) {
        return teacherService.create(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{uuid}")
    @Operation(
            summary = "Update teacher",
            description = "Update an existing teacher by their UUID. "
                    + "Only available for users with ADMIN role."
    )
    public TeacherDto update(@PathVariable UUID uuid,
                             @RequestBody UpdateTeacherRequestDto dto) {
        return teacherService.update(uuid, dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{uuid}")
    @Operation(
            summary = "Delete teacher",
            description = "Delete a teacher by their UUID. "
                    + "Only available for users with ADMIN role."
    )
    public TeacherDto delete(@PathVariable UUID uuid) {
        return teacherService.delete(uuid);
    }
}
