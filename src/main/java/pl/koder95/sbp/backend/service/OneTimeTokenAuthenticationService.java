package pl.koder95.sbp.backend.service;

import pl.koder95.sbp.backend.dto.EmailDeliveryInfoDto;
import pl.koder95.sbp.backend.dto.GenerateOneTimeTokenRequestDto;
import pl.koder95.sbp.backend.dto.StudentLoginRequestDto;
import pl.koder95.sbp.backend.dto.UserLoginResponseDto;

public interface OneTimeTokenAuthenticationService {
    EmailDeliveryInfoDto generateOtt(GenerateOneTimeTokenRequestDto requestDto);

    UserLoginResponseDto authenticate(StudentLoginRequestDto requestDto);
}
