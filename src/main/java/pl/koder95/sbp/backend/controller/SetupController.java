package pl.koder95.sbp.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.koder95.sbp.backend.dto.CreateAdminRequestDto;
import pl.koder95.sbp.backend.dto.CreateAdminResponseDto;
import pl.koder95.sbp.backend.service.SetupService;

@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
@Tag(name = "Setup controller",
        description = "This controller is used to init database after create its fresh instance")
public class SetupController {
    private final SetupService setupService;

    @PostMapping("/admin")
    @Operation(summary = "Create an admin account")
    public CreateAdminResponseDto createAdmin(
            @Valid @RequestBody CreateAdminRequestDto requestDto
    ) {
        return setupService.createAdmin(requestDto);
    }
}
