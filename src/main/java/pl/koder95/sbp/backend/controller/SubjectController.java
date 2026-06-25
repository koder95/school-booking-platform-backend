package pl.koder95.sbp.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import pl.koder95.sbp.backend.dto.CreateSubjectRequestDto;
import pl.koder95.sbp.backend.dto.SubjectDto;
import pl.koder95.sbp.backend.dto.UpdateSubjectRequestDto;
import pl.koder95.sbp.backend.service.SubjectService;

@SecurityRequirement(name = "bearer-key")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subjects")
@Tag(name = "Subject management", description = "The endpoint to manage subjects")
public class SubjectController {
    private final SubjectService subjectService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(
            summary = "Create a new subject",
            description = "Create a new subject. Only available for users with ADMIN role."
    )
    public SubjectDto create(@Valid @RequestBody CreateSubjectRequestDto requestDto) {
        return subjectService.create(requestDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping
    @Operation(
            summary = "Get all subjects",
            description = "Retrieve a paginated list of subjects. "
                    + "Available for users with ADMIN or STUDENT role."
    )
    public Page<SubjectDto> getAll(@ParameterObject Pageable pageable) {
        return subjectService.getAll(pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/{name:^(?!^[0-9]+$).+$}")
    @Operation(
            summary = "Get subject by name",
            description = "Retrieve a specific subject by its name. "
                    + "Available for users with ADMIN or STUDENT role."
    )
    public SubjectDto getByName(@PathVariable String name) {
        return subjectService.getByName(name);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/{id:[0-9]+}")
    @Operation(
            summary = "Get subject by ID",
            description = "Retrieve a specific subject by its ID. "
                    + "Available for users with ADMIN or STUDENT role."
    )
    public SubjectDto getOne(@PathVariable Long id) {
        return subjectService.get(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id:[0-9]+}")
    @Operation(
            summary = "Update subject",
            description = "Update an existing subject by its ID. "
                    + "Only available for users with ADMIN role."
    )
    public SubjectDto update(@PathVariable Long id,
                             @Valid @RequestBody UpdateSubjectRequestDto requestDto) {
        return subjectService.update(id, requestDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id:[0-9]+}")
    @Operation(
            summary = "Delete subject",
            description = "Delete a subject by its ID. "
                    + "Only available for users with ADMIN role."
    )
    public SubjectDto delete(@PathVariable Long id) {
        return subjectService.delete(id);
    }
}
