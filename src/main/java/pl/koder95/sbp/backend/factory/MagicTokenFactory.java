package pl.koder95.sbp.backend.factory;

import java.util.UUID;
import pl.koder95.sbp.backend.security.ml.MagicToken;

public interface MagicTokenFactory {
    MagicToken createMagicToken(String email, UUID notificationUuid);
}
