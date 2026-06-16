package pl.koder95.sbp.backend.service;

import java.util.UUID;
import pl.koder95.sbp.backend.dto.EmailValueDto;
import pl.koder95.sbp.backend.exception.InvalidMagicTokenException;
import pl.koder95.sbp.backend.security.ml.MagicLink;

public interface MagicLinkService {
    MagicLink createMagicLink(EmailValueDto dto, UUID notificationUuid);

    void validateMagicToken(String email, UUID notificationUuid, String token)
            throws InvalidMagicTokenException;
}
