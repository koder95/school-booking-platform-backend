package pl.koder95.sbp.backend.service.impl;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pl.koder95.sbp.backend.dto.EmailDeliveryInfoDto;
import pl.koder95.sbp.backend.dto.GenerateOneTimeTokenRequestDto;
import pl.koder95.sbp.backend.dto.SendEmailRequestDto;
import pl.koder95.sbp.backend.dto.StudentLoginRequestDto;
import pl.koder95.sbp.backend.dto.UserLoginResponseDto;
import pl.koder95.sbp.backend.exception.RequestRateLimitException;
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
    private final ProxyManager<Object> proxyManager;

    @Override
    public EmailDeliveryInfoDto generateOtt(GenerateOneTimeTokenRequestDto requestDto) {
        String email = requestDto.email();
        Bucket onePerMinute = proxyManager.builder()
                .build("ott:generate:" + email, onePerMinuteConfig());
        if (onePerMinute.tryConsume(1)) {
            Bucket fivePerHour = proxyManager.builder()
                    .build("ott:generate:" + email, fivePerHourConfig());
            if (!fivePerHour.tryConsume(1)) {
                throw new RequestRateLimitException("Request rate limit exceeded");
            }
            return consumeGenerateOttRequest(requestDto);
        }
        String ip = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest()
                .getRemoteAddr();
        Bucket fivePerFiveMinutes = proxyManager.builder()
                .build("ott:generate:" + email + ":" + ip, fivePerFiveMinutesConfig());
        if (fivePerFiveMinutes.tryConsume(1)) {
            return consumeGenerateOttRequest(requestDto);
        }
        throw new RequestRateLimitException(null);
    }

    private EmailDeliveryInfoDto consumeGenerateOttRequest(
            GenerateOneTimeTokenRequestDto requestDto
    ) {
        String email = requestDto.email();
        if (emailRepository.findByValue(email).isEmpty()) {
            Email saved = emailRepository.save(new Email().setValue(email));
            Student student = new Student();
            student.setEmail(saved);
            student.setZoneId(Objects.requireNonNull(requestDto.zoneId(),
                    "zone id is required for non existed student"
            ));
            studentRepository.save(student);
        }
        GenerateOneTimeTokenRequest request = new GenerateOneTimeTokenRequest(email);
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

    private Supplier<BucketConfiguration> onePerMinuteConfig() {
        return () -> BucketConfiguration.builder()
                .addLimit(
                        Bandwidth.builder()
                                .capacity(1)
                                .refillGreedy(1, Duration.ofMinutes(1))
                                .build()
                )
                .build();
    }

    private Supplier<BucketConfiguration> fivePerFiveMinutesConfig() {
        return () -> BucketConfiguration.builder()
                .addLimit(
                        Bandwidth.builder()
                                .capacity(5)
                                .refillIntervally(5, Duration.ofMinutes(5))
                                .build()
                )
                .build();
    }

    private Supplier<BucketConfiguration> fivePerHourConfig() {
        return () -> BucketConfiguration.builder()
                .addLimit(
                        Bandwidth.builder()
                                .capacity(5)
                                .refillIntervally(5, Duration.ofHours(1))
                                .build()
                )
                .build();
    }
}
