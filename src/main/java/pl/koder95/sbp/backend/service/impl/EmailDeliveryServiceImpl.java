package pl.koder95.sbp.backend.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.koder95.sbp.backend.dto.SendEmailRequestDto;
import pl.koder95.sbp.backend.model.DeliveryStatus;
import pl.koder95.sbp.backend.model.Email;
import pl.koder95.sbp.backend.model.EmailDeliveryLog;
import pl.koder95.sbp.backend.repository.EmailDeliveryLogRepository;
import pl.koder95.sbp.backend.repository.EmailRepository;
import pl.koder95.sbp.backend.service.EmailDeliveryService;

@Service
@RequiredArgsConstructor
public class EmailDeliveryServiceImpl implements EmailDeliveryService {
    private final JavaMailSender mailSender;
    private final EmailRepository emailRepository;
    private final EmailDeliveryLogRepository logRepository;

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
            helper.setText(dto.body());
            mailSender.send(mimeMessage);
            updateStatus(deliveryLog, DeliveryStatus.SENT, null);
        } catch (RuntimeException | MessagingException | UnsupportedEncodingException e) {
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(errorStream));
            updateStatus(deliveryLog, DeliveryStatus.FAILED, errorStream.toString());
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
}
