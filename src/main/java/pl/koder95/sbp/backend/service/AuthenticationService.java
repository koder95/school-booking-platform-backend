package pl.koder95.sbp.backend.service;

import pl.koder95.sbp.backend.dto.UserLoginRequestDto;
import pl.koder95.sbp.backend.dto.UserLoginResponseDto;

public interface AuthenticationService {
    UserLoginResponseDto login(UserLoginRequestDto request);
}
