package pl.koder95.sbp.backend.service.impl;

import jakarta.mail.internet.MimeMessage;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Slf4j
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
    public void requestAsyncSend(@Validated SendEmailRequestDto dto) {
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
        logRepository.save(log);
    }

    private MimeMessage createMimeMessage(EmailDeliveryLog log) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        try {
            helper.setFrom(mailFrom, "School Booking Platform");
            helper.setTo(log.getRecipient().getValue());
            helper.setSubject(log.getSubject());
            helper.setText(log.getBody(), true);
        } catch (Exception e) {
            log.setStatus(DeliveryStatus.FAILED);
            log.setErrorMessage(e.getMessage());
            throw new EmailDeliveryException("Cannot create a message");
        }
        return mimeMessage;
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
            requestAsyncSend(new SendEmailRequestDto(username, "Authenticate yourself", emailBody));
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

    @Override
    @Transactional
    public void sendAll() {
        logRepository.findByStatus(DeliveryStatus.FAILED)
                .forEach(log -> log.setStatus(DeliveryStatus.PENDING));
        List<EmailDeliveryLog> pending = logRepository.findByStatus(DeliveryStatus.PENDING);
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            executorService.invokeAll(pending.stream()
                    .map(this::createCallable)
                    .toList()
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted sending emails", e);
        }
    }

    @Override
    public int countPendingEmails() {
        return logRepository.countByStatusIn(Set.of(DeliveryStatus.PENDING));
    }

    private Callable<EmailDeliveryLog> createCallable(EmailDeliveryLog deliveryLog) {
        return () -> {
            try {
                log.info("Sending email...");
                mailSender.send(createMimeMessage(deliveryLog));
                deliveryLog.setStatus(DeliveryStatus.SENT);
                log.info("successfully sent to #{}", deliveryLog.getRecipient());
            } catch (MailException e) {
                deliveryLog.setStatus(DeliveryStatus.FAILED);
                deliveryLog.setErrorMessage(e.getMessage());
                log.info("failed sent to #{}", deliveryLog.getRecipient().getId());
            }
            return deliveryLog;
        };
    }
}
