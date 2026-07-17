package pl.koder95.sbp.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.koder95.sbp.backend.dto.AvailabilitySlotDto;
import pl.koder95.sbp.backend.service.AvailabilitySlotService;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@PreAuthorize("hasAnyRole('ADMIN')")
@RequestMapping("/api/availability-slots")
@Tag(name = "Availability slots management",
        description = "The endpoint to manage availability slots")
public class AvailabilitySlotController {
    private final AvailabilitySlotService availabilitySlotService;

    @GetMapping
    @Operation(summary = "Get all availability slots",
            description = "Retrieve a paginated list of availability slots.")
    public Page<AvailabilitySlotDto> getAll(@ParameterObject Pageable pageable) {
        return availabilitySlotService.getAll(pageable);
    }

    @PostMapping
    @Operation(
            summary = "Generate availability slots",
            description = "Generate availability slots for a every teacher. "
                    + "This operation may spend much time."
    )
    public Page<AvailabilitySlotDto> generate(@ParameterObject Pageable pageable) {
        return availabilitySlotService.createOrGetAll(pageable);
    }
}
