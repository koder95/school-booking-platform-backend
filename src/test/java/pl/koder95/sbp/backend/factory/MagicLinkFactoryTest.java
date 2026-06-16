package pl.koder95.sbp.backend.factory;

import java.time.ZonedDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.koder95.sbp.backend.config.MagicLinkConfig;
import pl.koder95.sbp.backend.factory.impl.MagicLinkFactoryImpl;
import pl.koder95.sbp.backend.security.ml.MagicLink;
import pl.koder95.sbp.backend.security.ml.MagicToken;
import pl.koder95.sbp.backend.security.ml.MagicTokenSerializer;

@ExtendWith(MockitoExtension.class)
public class MagicLinkFactoryTest {
    @InjectMocks
    private MagicLinkFactoryImpl magicLinkFactory;
    @Mock
    private MagicLinkConfig magicLinkConfig;
    @Mock
    private MagicTokenSerializer magicTokenSerializer;
    @Mock
    private MagicTokenFactory magicTokenFactory;

    @Test
    void createMagicLink_validInput_ok() {
        String baseUrl = "http://localhost:8080";
        Mockito.when(magicLinkConfig.baseUrl()).thenReturn(baseUrl);
        String frontendEndpoint = "login/link";
        Mockito.when(magicLinkConfig.frontendEndpoint()).thenReturn(frontendEndpoint);
        String paramName = "token";
        Mockito.when(magicLinkConfig.paramName()).thenReturn(paramName);
        String email = "student@example.com";
        ZonedDateTime createdAt = ZonedDateTime.now();
        UUID notificationUuid = UUID.fromString("ab6f448b-c0cb-4ec3-8e54-a9c98c9c6de9");
        MagicToken token = new MagicToken(email, createdAt, notificationUuid);
        Mockito.when(magicTokenFactory.createMagicToken(Mockito.anyString(), Mockito.any()))
                .thenReturn(token);
        String serialized = "K3puMlBnQWtLRHg3NlBOQXZTT0tVang4RU5MZFdGSksxUzBjcTR3OGVuWT06Ojp0T3pEb"
                + "Ep1c3Jnc2RpalJIU2xuaEVBREhNT0xNTC8vV0lNZ3NzbjRoaFUxRTd0MElsNjhURjBxMjQ3UFZtWENp"
                + "ZVFzbVA1dHhlZGRYS2d4MUlYWkttQT09Ojo6MllRVFAzNzZCWGhrNkJPcnVoZHlqZGJ2YTdtYXUxRkN"
                + "yZzBJdWtjZXZ5WU1DQkd6UFMycUs5ZVZVVjJJeDRXMg";
        Mockito.when(magicTokenSerializer.serialize(Mockito.any())).thenReturn(serialized);
        MagicLink actual = magicLinkFactory.createMagicLink(email, notificationUuid);
        System.out.println(actual);
    }
}
