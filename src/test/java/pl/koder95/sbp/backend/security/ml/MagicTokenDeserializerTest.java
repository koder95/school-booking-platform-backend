package pl.koder95.sbp.backend.security.ml;

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
import pl.koder95.sbp.backend.security.ml.impl.SecuredMagicTokenDeserializer;

@ExtendWith(MockitoExtension.class)
public class MagicTokenDeserializerTest {
    @InjectMocks
    private SecuredMagicTokenDeserializer magicTokenDeserializer;
    @Mock
    private MagicLinkConfig magicLinkConfig;

    @Test
    void deserialize_nullToken_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> magicTokenDeserializer.deserialize(""));
        Mockito.verifyNoInteractions(magicLinkConfig);
    }

    @Test
    void deserialize_emptyToken_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> magicTokenDeserializer.deserialize(""));
        Mockito.verifyNoInteractions(magicLinkConfig);
    }

    @Test
    void deserialize_validToken_ok() {
        Mockito.when(magicLinkConfig.secretKey()).thenReturn("secretKey");
        String token = "K3puMlBnQWtLRHg3NlBOQXZTT0tVang4RU5MZFdGSksxUzBjcTR3OGVuWT06Ojp0T3pEbEp1c3"
                + "Jnc2RpalJIU2xuaEVBREhNT0xNTC8vV0lNZ3NzbjRoaFUxRTd0MElsNjhURjBxMjQ3UFZtWENpZVFzb"
                + "VA1dHhlZGRYS2d4MUlYWkttQT09Ojo6MllRVFAzNzZCWGhrNkJPcnVoZHlqZGJ2YTdtYXUxRkNyZzBJ"
                + "dWtjZXZ5WU1DQkd6UFMycUs5ZVZVVjJJeDRXMg";
        MagicToken expected = new MagicToken(
                "student@example.com",
                ZonedDateTime.parse("2026-06-16T11:41:59.807348900+02:00[Europe/Warsaw]"),
                UUID.fromString("ab6f448b-c0cb-4ec3-8e54-a9c98c9c6de9")
        );
        MagicToken actual = magicTokenDeserializer.deserialize(token);
        assertEquals(expected, actual);
    }
}
