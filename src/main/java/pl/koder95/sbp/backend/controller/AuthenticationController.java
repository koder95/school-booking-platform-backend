package pl.koder95.sbp.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.koder95.sbp.backend.dto.EmailDeliveryInfoDto;
import pl.koder95.sbp.backend.dto.GenerateOneTimeTokenRequestDto;
import pl.koder95.sbp.backend.dto.StudentLoginRequestDto;
import pl.koder95.sbp.backend.dto.UserLoginRequestDto;
import pl.koder95.sbp.backend.dto.UserLoginResponseDto;
import pl.koder95.sbp.backend.service.AuthenticationService;
import pl.koder95.sbp.backend.service.OneTimeTokenAuthenticationService;

@Tag(name = "Authentication management",
        description = "Provide authentication abilities and user management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final OneTimeTokenAuthenticationService oneTimeTokenAuthenticationService;

    @Operation(summary = "Generate access token for administrators")
    @PostMapping("/login")
    public UserLoginResponseDto login(@Valid @RequestBody UserLoginRequestDto requestDto) {
        return authenticationService.login(requestDto);
    }

    @Operation(summary = "Generate access token for students")
    @PostMapping("/ott")
    public UserLoginResponseDto login(@RequestBody StudentLoginRequestDto requestDto) {
        return oneTimeTokenAuthenticationService.authenticate(requestDto);
    }

    @Operation(summary = "Generate OTT token and send it")
    @PostMapping("/ott/generate")
    public EmailDeliveryInfoDto sendOtt(GenerateOneTimeTokenRequestDto request) {
        return oneTimeTokenAuthenticationService.generateOtt(request);
    }
}
