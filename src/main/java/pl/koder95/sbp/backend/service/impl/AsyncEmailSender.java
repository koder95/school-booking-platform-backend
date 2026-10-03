package pl.koder95.sbp.backend.service.impl;

import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.koder95.sbp.backend.exception.EmailDeliveryException;

@Slf4j
@RequiredArgsConstructor
@Component
public class AsyncEmailSender {
    private final JavaMailSender mailSender;

    @Async
    public void sendAsync(MimeMessage mimeMessage) {
        log.info("Sending e-mail...");
        Address[] allRecipients;
        try {
            allRecipients = mimeMessage.getAllRecipients();
        } catch (MessagingException e) {
            throw new EmailDeliveryException("Cannot sent e-mail", e);
        }
        try {
            mailSender.send(mimeMessage);
            log.info("Sent e-mail to {}", hashEmailAddresses(allRecipients));
        } catch (Exception e) {
            log.error("Not sent e-mail to {}", hashEmailAddresses(allRecipients));
            throw new EmailDeliveryException("Cannot sent e-mail", e);
        }
    }

    private String hashEmailAddresses(Address[] allRecipients) {
        return Arrays.stream(allRecipients)
                .map(Address::toString)
                .map(s -> new EmailBasicInfo(s.indexOf("@"), s.substring(s.indexOf("@") + 1)))
                .map(ebi -> String.format(
                        "%s@%s", createPlaceholder(ebi.charsBeforeAt), ebi.domain
                ))
                .collect(Collectors.joining(","));
    }

    private String createPlaceholder(int length) {
        return IntStream.range(0, length)
                .mapToObj(i -> String.valueOf('*'))
                .collect(Collectors.joining());
    }

    private record EmailBasicInfo(int charsBeforeAt, String domain) {}
}
