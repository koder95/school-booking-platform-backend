package pl.koder95.sbp.backend.service.impl;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.stereotype.Service;
import pl.koder95.sbp.backend.dto.EmailDeliveryInfoDto;
import pl.koder95.sbp.backend.dto.GenerateOneTimeTokenRequestDto;
import pl.koder95.sbp.backend.dto.SendEmailRequestDto;
import pl.koder95.sbp.backend.dto.StudentLoginRequestDto;
import pl.koder95.sbp.backend.dto.UserLoginResponseDto;
import pl.koder95.sbp.backend.model.Email;
import pl.koder95.sbp.backend.model.Student;
import pl.koder95.sbp.backend.repository.EmailRepository;
import pl.koder95.sbp.backend.repository.StudentRepository;
import pl.koder95.sbp.backend.security.JwtUtil;
import pl.koder95.sbp.backend.service.EmailDeliveryService;
import pl.koder95.sbp.backend.service.OneTimeTokenAuthenticationService;
import pl.koder95.sbp.backend.service.OneTimeTokenDeliveryService;

@Service
@RequiredArgsConstructor
public class OneTimeTokenAuthenticationServiceImpl implements OneTimeTokenAuthenticationService {
    private final OneTimeTokenService oneTimeTokenService;
    private final JwtUtil jwtUtil;
    private final EmailDeliveryService emailDeliveryService;
    private final OneTimeTokenDeliveryService tokenDeliveryService;
    private final EmailRepository emailRepository;
    private final StudentRepository studentRepository;

    @Override
    public EmailDeliveryInfoDto generateOtt(GenerateOneTimeTokenRequestDto requestDto) {
        if (emailRepository.findByValue(requestDto.email()).isEmpty()) {
            Email saved = emailRepository.save(new Email().setValue(requestDto.email()));
            Student student = new Student();
            student.setEmail(saved);
            student.setZoneId(Objects.requireNonNull(requestDto.zoneId(),
                    "zone id is required for non existed student"
            ));
            studentRepository.save(student);
        }
        GenerateOneTimeTokenRequest request = new GenerateOneTimeTokenRequest(requestDto.email());
        OneTimeToken generated = oneTimeTokenService.generate(request);
        return tokenDeliveryService.deliver(generated);
    }

    @Override
    public UserLoginResponseDto authenticate(StudentLoginRequestDto requestDto) {
        OneTimeToken consumed = oneTimeTokenService.consume(
                new OneTimeTokenAuthenticationToken(requestDto.token())
        );
        if (consumed == null) {
            throw new IllegalStateException("invalid token");
        }
        Student principal = studentRepository.findByEmail(consumed.getUsername()).orElseThrow();
        String jwt = jwtUtil.generateToken(principal.getUsername());
        emailDeliveryService.send(new SendEmailRequestDto(
                principal.getUsername(), "Login notification",
                "A new login was detected using your email address. If this wasn't you, please "
                        + "contact the administrator."
        ));
        return new UserLoginResponseDto(jwt);
    }
}
