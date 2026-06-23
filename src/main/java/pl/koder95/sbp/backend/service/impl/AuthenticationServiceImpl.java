package pl.koder95.sbp.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.koder95.sbp.backend.dto.SendEmailRequestDto;
import pl.koder95.sbp.backend.dto.UserLoginRequestDto;
import pl.koder95.sbp.backend.dto.UserLoginResponseDto;
import pl.koder95.sbp.backend.security.JwtUtil;
import pl.koder95.sbp.backend.service.AuthenticationService;
import pl.koder95.sbp.backend.service.EmailDeliveryService;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailDeliveryService emailDeliveryService;

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto request) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        String token = jwtUtil.generateToken(authenticate.getName());
        emailDeliveryService.send(new SendEmailRequestDto(
                request.email(), "Login notification",
                "A new login was detected using your email address. If this wasn't you, please "
                        + "secure your account immediately and contact the administrator."
        ));
        return new UserLoginResponseDto(token);
    }
}
