package pl.koder95.sbp.backend.service;

import org.springframework.security.authentication.ott.OneTimeToken;
import pl.koder95.sbp.backend.dto.EmailDeliveryInfoDto;

public interface OneTimeTokenDeliveryService {
    EmailDeliveryInfoDto deliver(OneTimeToken generated);
}
