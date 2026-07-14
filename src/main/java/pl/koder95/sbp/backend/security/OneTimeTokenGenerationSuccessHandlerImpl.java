package pl.koder95.sbp.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.web.authentication.ott.OneTimeTokenGenerationSuccessHandler;
import org.springframework.stereotype.Component;
import pl.koder95.sbp.backend.config.MagicLinkConfig;
import pl.koder95.sbp.backend.dto.SendEmailRequestDto;
import pl.koder95.sbp.backend.service.EmailDeliveryService;

@Component
@RequiredArgsConstructor
public class OneTimeTokenGenerationSuccessHandlerImpl
        implements OneTimeTokenGenerationSuccessHandler {
    private final EmailDeliveryService deliveryService;
    private final MagicLinkConfig magicLinkConfig;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       OneTimeToken oneTimeToken) {
        String magicLink = "%s/%s?%s=%s".formatted(
                magicLinkConfig.baseUrl(),
                magicLinkConfig.frontendEndpoint(),
                magicLinkConfig.paramName(),
                oneTimeToken.getTokenValue()
        );
        String emailBody = "<html><body><p>Your link: <a href=\"%s\">%s</a></p></body></html>"
                .formatted(magicLink, magicLink);
        deliveryService.send(new SendEmailRequestDto(oneTimeToken.getUsername(),
                "Authenticate yourself", emailBody));
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
