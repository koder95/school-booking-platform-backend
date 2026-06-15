package pl.koder95.sbp.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.koder95.sbp.backend.dto.UserLoginRequestDto;
import pl.koder95.sbp.backend.dto.UserLoginResponseDto;
import pl.koder95.sbp.backend.service.AuthenticationService;

@Tag(name = "Authentication management",
        description = "Provide authentication abilities and user management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Generate access token")
    @PostMapping("/login")
    public UserLoginResponseDto login(@Valid @RequestBody UserLoginRequestDto request) {
        return authenticationService.login(request);
    }
}
