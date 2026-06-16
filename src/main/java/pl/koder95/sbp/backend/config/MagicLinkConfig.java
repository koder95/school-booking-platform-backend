package pl.koder95.sbp.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public record MagicLinkConfig(
        @Value("${magic-link.expiration-time}") long expirationTime,
        @Value("${magic-link.secret}") String secretKey,
        @Value("${magic-link.base-url}") String baseUrl,
        @Value("${magic-link.frontend-endpoint}") String frontendEndpoint,
        @Value("${magic-link.paramName}") String paramName
) {
}
