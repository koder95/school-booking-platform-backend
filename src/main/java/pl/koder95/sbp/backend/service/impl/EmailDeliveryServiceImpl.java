package pl.koder95.sbp.backend.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.koder95.sbp.backend.config.MagicLinkConfig;
import pl.koder95.sbp.backend.dto.EmailDeliveryInfoDto;
import pl.koder95.sbp.backend.dto.SendEmailRequestDto;
import pl.koder95.sbp.backend.exception.EmailDeliveryException;
import pl.koder95.sbp.backend.model.DeliveryStatus;
import pl.koder95.sbp.backend.model.Email;
import pl.koder95.sbp.backend.model.EmailDeliveryLog;
import pl.koder95.sbp.backend.repository.EmailDeliveryLogRepository;
import pl.koder95.sbp.backend.repository.EmailRepository;
import pl.koder95.sbp.backend.service.EmailDeliveryService;
import pl.koder95.sbp.backend.service.OneTimeTokenDeliveryService;

@Service
@RequiredArgsConstructor
public class EmailDeliveryServiceImpl
        implements EmailDeliveryService, OneTimeTokenDeliveryService {
    private final JavaMailSender mailSender;
    private final EmailRepository emailRepository;
    private final EmailDeliveryLogRepository logRepository;
    private final MagicLinkConfig magicLinkConfig;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Override
    public void send(@Validated SendEmailRequestDto dto) {
        EmailDeliveryLog deliveryLog = prepareSend(dto);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        try {
            helper.setFrom(mailFrom, "School Booking Platform");
            helper.setTo(dto.recipient());
            helper.setSubject(dto.subject());
            helper.setText(dto.body(), true);
            mailSender.send(mimeMessage);
            updateStatus(deliveryLog, DeliveryStatus.SENT, null);
        } catch (RuntimeException | MessagingException | UnsupportedEncodingException e) {
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(errorStream));
            updateStatus(deliveryLog, DeliveryStatus.FAILED, errorStream.toString());
            throw new EmailDeliveryException(
                    "Email wasn't sent (log #%d).".formatted(deliveryLog.getId())
            );
        } finally {
            logRepository.save(deliveryLog);
        }
    }

    private void updateStatus(EmailDeliveryLog deliveryLog,
                              DeliveryStatus status, String errorMessage) {
        deliveryLog.setStatus(status);
        deliveryLog.setErrorMessage(errorMessage);
    }

    private EmailDeliveryLog prepareSend(SendEmailRequestDto dto) {
        Objects.requireNonNull(dto);
        EmailDeliveryLog log = new EmailDeliveryLog();
        log.setSubject(dto.subject());
        log.setBody(dto.body());
        log.setStatus(DeliveryStatus.PENDING);
        String recipientEmail = dto.recipient();
        log.setRecipient(emailRepository.findByValue(recipientEmail).orElseGet(() -> {
            Email created = new Email();
            created.setValue(recipientEmail);
            return emailRepository.save(created);
        }));
        return logRepository.save(log);
    }

    @Override
    public EmailDeliveryInfoDto deliver(OneTimeToken token) {
        String username = token.getUsername();
        ZonedDateTime createdAt = ZonedDateTime.now();
        String magicLink = "%s/%s?%s=%s".formatted(
                magicLinkConfig.baseUrl(),
                magicLinkConfig.frontendEndpoint(),
                magicLinkConfig.paramName(),
                token.getTokenValue()
        );
        String emailBody = "<html><body><p>Your link: <a href=\"%s\">%s</a></p></body></html>"
                .formatted(magicLink, magicLink);
        try {
            send(new SendEmailRequestDto(username, "Authenticate yourself", emailBody));
        } catch (Exception e) {
            return new EmailDeliveryInfoDto(
                    createdAt, DeliveryStatus.FAILED, "token delivery failed", username
            );
        }
        return new EmailDeliveryInfoDto(
                createdAt, DeliveryStatus.SENT, null, username
        );
    }

    @Override
    public Page<EmailDeliveryInfoDto> getAll(Pageable pageable) {
        return logRepository.findAll(pageable).map(log -> new EmailDeliveryInfoDto(
                log.getCreatedAt(),
                log.getStatus(),
                log.getErrorMessage(),
                log.getRecipient().getValue()
        ));
    }
}
