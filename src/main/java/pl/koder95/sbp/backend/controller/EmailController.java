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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.koder95.sbp.backend.dto.EmailDto;
import pl.koder95.sbp.backend.service.EmailService;

@SecurityRequirement(name = "bearer-key")
@Tag(name = "Email retrieval", description = "The endpoint for retrieving emails")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/emails")
public class EmailController {
    private final EmailService emailService;

    @Operation(summary = "Get all emails",
            description = "Retrieve a paginated list of emails")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<EmailDto> getAll(@ParameterObject Pageable pageable) {
        return emailService.getAll(pageable);
    }
}
