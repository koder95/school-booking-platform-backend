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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.koder95.sbp.backend.dto.BookingDecisionDto;
import pl.koder95.sbp.backend.dto.BookingDto;
import pl.koder95.sbp.backend.service.BookingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Booking controller",
        description = "This controller is used to manage bookings")
public class BookingController {
    private final BookingService service;

    @GetMapping
    @Operation(summary = "Get all bookings", description = "Retrieve a list of all bookings")
    public Page<BookingDto> getAllBookings(@ParameterObject Pageable pageable) {
        return service.getAll(pageable);
    }

    @GetMapping("/not-accepted-yet")
    @Operation(summary = "Get all not accepted yet bookings",
            description = "Retrieve a list of all not accepted yet bookings")
    public Page<BookingDto> getAllNotAcceptedYetBookings(@ParameterObject Pageable pageable) {
        return service.getAllNotAcceptedYetBookings(pageable);
    }

    @PostMapping("/decision")
    @Operation(summary = "Apply booking decision", description = "Apply a decision to a booking")
    public Page<BookingDto> applyDecision(@Valid @RequestParam BookingDecisionDto decision,
                                          @ParameterObject Pageable pageable) {
        return service.applyDecision(decision, pageable);
    }
}
