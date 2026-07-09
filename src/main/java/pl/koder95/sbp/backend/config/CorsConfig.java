package pl.koder95.sbp.backend.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public record CorsConfig(
        @Value("#{'${sbp.cors.allowed-origins}'.split(';')}") List<String> allowedOrigins
) {
}
