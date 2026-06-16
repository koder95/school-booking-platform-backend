package pl.koder95.sbp.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import pl.koder95.sbp.backend.dto.EmailValueDto;
import pl.koder95.sbp.backend.exception.InvalidMagicTokenException;
import pl.koder95.sbp.backend.factory.MagicLinkFactory;
import pl.koder95.sbp.backend.security.ml.MagicLink;
import pl.koder95.sbp.backend.security.ml.MagicToken;
import pl.koder95.sbp.backend.security.ml.MagicTokenDeserializer;
import pl.koder95.sbp.backend.service.impl.MagicLinkServiceImpl;

@ExtendWith(MockitoExtension.class)
public class MagicLinkServiceTest {
    @InjectMocks
    private MagicLinkServiceImpl magicLinkService;
    @Mock
    private MagicLinkFactory magicLinkFactory;
    @Mock
    private MagicLinkConfig magicLinkConfig;
    @Mock
    private MagicTokenDeserializer magicTokenDeserializer;

    @Test
    void createMagicLink_nullInputs_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> magicLinkService.createMagicLink(null, null)
        );
        Mockito.verifyNoInteractions(magicLinkFactory, magicTokenDeserializer, magicLinkConfig);
    }

    @Test
    void createMagicLink_nullDto_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> magicLinkService.createMagicLink(null, UUID.randomUUID())
        );
        Mockito.verifyNoInteractions(magicLinkFactory, magicTokenDeserializer, magicLinkConfig);
    }

    @Test
    void createMagicLink_nullUuid_throwsIllegalArgumentException() {
        EmailValueDto dto = new EmailValueDto("student@example.com");
        assertThrows(IllegalArgumentException.class,
                () -> magicLinkService.createMagicLink(dto, null)
        );
        Mockito.verifyNoInteractions(magicLinkFactory, magicTokenDeserializer, magicLinkConfig);
    }

    @Test
    void createMagicLink_validInputs_ok() {
        MagicLink expected = new MagicLink(
                "http://localhost:8080", "login/link", "token", "generated"
        );
        Mockito.when(magicLinkFactory.createMagicLink(Mockito.anyString(), Mockito.any()))
                .thenReturn(expected);
        String email = "student@example.com";
        EmailValueDto dto = new EmailValueDto(email);
        UUID notificationUuid = UUID.randomUUID();
        MagicLink actual = magicLinkService.createMagicLink(dto, notificationUuid);
        assertEquals(expected, actual);
        Mockito.verifyNoMoreInteractions(magicLinkFactory);
        Mockito.verifyNoInteractions(magicTokenDeserializer, magicLinkConfig);
    }

    @Test
    void validateMagicToken_nullsInput_throwsInvalidMagicTokenException() {
        assertThrows(InvalidMagicTokenException.class,
                () -> magicLinkService.validateMagicToken(null, null, null)
        );
        Mockito.verifyNoInteractions(magicLinkFactory, magicTokenDeserializer, magicLinkConfig);
    }

    @Test
    void validateMagicToken_onlyExpiredToken_throwsInvalidMagicTokenException() {
        long expirationTime = 1L;
        ZonedDateTime createdAt = ZonedDateTime.now().minusSeconds(2 * expirationTime);
        MagicToken expired = new MagicToken(
                "student@example.com", createdAt, UUID.randomUUID()
        );
        String token = "token";
        Mockito.when(magicTokenDeserializer.deserialize(token)).thenReturn(expired);
        Mockito.when(magicLinkConfig.expirationTime()).thenReturn(expirationTime);
        assertThrows(InvalidMagicTokenException.class,
                () -> magicLinkService.validateMagicToken(null, null, token)
        );
        Mockito.verifyNoMoreInteractions(magicTokenDeserializer, magicLinkConfig);
        Mockito.verifyNoInteractions(magicLinkFactory);
    }

    @Test
    void validateMagicToken_onlyMismatchedEmail_throwsInvalidMagicTokenException() {
        UUID notificationUuid = UUID.randomUUID();
        MagicToken deserialized = new MagicToken(
                "student@example.com", ZonedDateTime.now(), notificationUuid
        );
        String token = "token";
        Mockito.when(magicTokenDeserializer.deserialize(token)).thenReturn(deserialized);
        Mockito.when(magicLinkConfig.expirationTime()).thenReturn(3600L);
        String wrongEmail = "noreply@example.com";
        assertThrows(InvalidMagicTokenException.class,
                () -> magicLinkService.validateMagicToken(wrongEmail, notificationUuid, token)
        );
        Mockito.verifyNoMoreInteractions(magicTokenDeserializer, magicLinkConfig);
        Mockito.verifyNoInteractions(magicLinkFactory);
    }

    @Test
    void validateMagicToken_onlyMismatchedUuid_throwsInvalidMagicTokenException() {
        String email = "student@example.com";
        MagicToken deserialized = new MagicToken(
                email, ZonedDateTime.now(), UUID.randomUUID()
        );
        String token = "token";
        Mockito.when(magicTokenDeserializer.deserialize(token)).thenReturn(deserialized);
        Mockito.when(magicLinkConfig.expirationTime()).thenReturn(3600L);
        UUID wrongUuid = UUID.randomUUID();
        assertThrows(InvalidMagicTokenException.class,
                () -> magicLinkService.validateMagicToken(email, wrongUuid, token)
        );
        Mockito.verifyNoMoreInteractions(magicTokenDeserializer, magicLinkConfig);
        Mockito.verifyNoInteractions(magicLinkFactory);
    }

    @Test
    void validateMagicToken_validInputs_ok() throws InvalidMagicTokenException {
        MagicToken deserialized = new MagicToken(
                "student@example.com", ZonedDateTime.now(), UUID.randomUUID()
        );
        String token = "token";
        Mockito.when(magicTokenDeserializer.deserialize(token)).thenReturn(deserialized);
        Mockito.when(magicLinkConfig.expirationTime()).thenReturn(3600L);
        magicLinkService.validateMagicToken(
                deserialized.email(), deserialized.notificationUuid(), token
        );
        Mockito.verifyNoMoreInteractions(magicTokenDeserializer, magicLinkConfig);
        Mockito.verifyNoInteractions(magicLinkFactory);
    }
}
