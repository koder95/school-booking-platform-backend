package pl.koder95.sbp.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.koder95.sbp.backend.dto.BookingDto;
import pl.koder95.sbp.backend.service.SetupService;

@Profile("!production")
@RestController
@RequestMapping("/api/setup/examples")
@RequiredArgsConstructor
@Tag(name = "Examples controller",
        description = "This controller is used to insert example entities")
public class ExamplesController {
    private final SetupService setupService;

    @PostMapping("/install")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Install example data",
            description = "This endpoint is used to install example data into the database. "
                + "It is protected by admin role, so only admin can use it.")
    public Page<BookingDto> installExamples(
            @RequestParam(required = false, defaultValue = "1")
            @Valid @Positive @Max(7) Integer step,
            @ParameterObject Pageable pageable
    ) {
        return setupService.installExamples(step, pageable);
    }
}
