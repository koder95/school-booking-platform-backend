package pl.koder95.sbp.backend.factory;

import java.util.UUID;
import pl.koder95.sbp.backend.security.ml.MagicLink;

public interface MagicLinkFactory {
    MagicLink createMagicLink(String emailAddress, UUID notificationUuid);
}
