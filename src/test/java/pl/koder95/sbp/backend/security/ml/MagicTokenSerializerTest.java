package pl.koder95.sbp.backend.security.ml;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZonedDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.koder95.sbp.backend.config.MagicLinkConfig;
import pl.koder95.sbp.backend.security.ml.impl.SecuredMagicTokenSerializer;

@ExtendWith(MockitoExtension.class)
public class MagicTokenSerializerTest {
    @InjectMocks
    private SecuredMagicTokenSerializer magicTokenSerializer;
    @Mock
    private MagicLinkConfig magicLinkConfig;

    @Test
    void serialize_nullMagicToken_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> magicTokenSerializer.serialize(null));
        Mockito.verifyNoInteractions(magicLinkConfig);
    }

    @Test
    void serialize_magicTokenWithNulls_throwsIllegalArgumentException() {
        MagicToken token = new MagicToken(null, null, null);
        assertThrows(IllegalArgumentException.class, () -> magicTokenSerializer.serialize(token));
        Mockito.verifyNoInteractions(magicLinkConfig);
    }

    @Test
    void serialize_magicTokenWithNullEmail_throwsIllegalArgumentException() {
        String email = null;
        ZonedDateTime createdAt = ZonedDateTime.now();
        UUID notificationUuid = UUID.randomUUID();
        MagicToken token = new MagicToken(email, createdAt, notificationUuid);
        assertThrows(IllegalArgumentException.class, () -> magicTokenSerializer.serialize(token));
        Mockito.verifyNoInteractions(magicLinkConfig);
    }

    @Test
    void serialize_magicTokenWithNullCreatedAt_throwsIllegalArgumentException() {
        String email = "student@example.com";
        ZonedDateTime createdAt = null;
        UUID notificationUuid = UUID.randomUUID();
        MagicToken token = new MagicToken(email, createdAt, notificationUuid);
        assertThrows(IllegalArgumentException.class, () -> magicTokenSerializer.serialize(token));
        Mockito.verifyNoInteractions(magicLinkConfig);
    }

    @Test
    void serialize_magicTokenWithNullNotificationUuid_throwsIllegalArgumentException() {
        String email = "student@example.com";
        ZonedDateTime createdAt = ZonedDateTime.now();
        UUID notificationUuid = null;
        MagicToken token = new MagicToken(email, createdAt, notificationUuid);
        assertThrows(IllegalArgumentException.class, () -> magicTokenSerializer.serialize(token));
        Mockito.verifyNoInteractions(magicLinkConfig);
    }

    @Test
    void serialize_correctMagicToken_ok() {
        Mockito.when(magicLinkConfig.secretKey()).thenReturn("secretKey");
        String email = "student@example.com";
        ZonedDateTime createdAt = ZonedDateTime.now();
        UUID notificationUuid = UUID.randomUUID();
        MagicToken magicToken = new MagicToken(email, createdAt, notificationUuid);
        String actual = magicTokenSerializer.serialize(magicToken);
        System.out.println(actual);
    }
}
