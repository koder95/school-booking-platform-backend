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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.koder95.sbp.backend.dto.CreateStudentRequestDto;
import pl.koder95.sbp.backend.dto.StudentDto;
import pl.koder95.sbp.backend.service.StudentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/students")
@Tag(name = "Student management", description = "Endpoints for managing students.")
public class StudentController {
    private final StudentService studentService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Get all students",
            description = "Retrieve a paginated list of students. "
                    + "Only available for users with ADMIN role.")
    public Page<StudentDto> getAll(@ParameterObject Pageable pageable) {
        return studentService.getAll(pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Create a new student",
            description = "Create a new student with the provided email address. "
                    + "Only available for users with ADMIN role.")
    public StudentDto create(@RequestBody CreateStudentRequestDto requestDto) {
        return studentService.create(requestDto);
    }

    @GetMapping("/{studentUuid}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Get a student by UUID",
            description = "Retrieve a student by their unique identifier. "
                    + "Only available for users with ADMIN role.")
    public StudentDto get(@PathVariable UUID studentUuid) {
        return studentService.get(studentUuid);
    }
}
