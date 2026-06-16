package pl.koder95.sbp.backend.factory.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.koder95.sbp.backend.config.MagicLinkConfig;
import pl.koder95.sbp.backend.factory.MagicLinkFactory;
import pl.koder95.sbp.backend.factory.MagicTokenFactory;
import pl.koder95.sbp.backend.security.ml.MagicLink;
import pl.koder95.sbp.backend.security.ml.MagicToken;
import pl.koder95.sbp.backend.security.ml.MagicTokenSerializer;

@Component
@RequiredArgsConstructor
public class MagicLinkFactoryImpl implements MagicLinkFactory {
    private final MagicLinkConfig magicLinkConfig;
    private final MagicTokenSerializer magicTokenSerializer;
    private final MagicTokenFactory magicTokenFactory;

    @Override
    public MagicLink createMagicLink(String emailAddress, UUID notificationUuid) {
        MagicToken token = magicTokenFactory.createMagicToken(emailAddress, notificationUuid);
        return new MagicLink(
                magicLinkConfig.baseUrl(),
                magicLinkConfig.frontendEndpoint(),
                magicLinkConfig.paramName(),
                magicTokenSerializer.serialize(token)
        );
    }
}
