package pl.koder95.sbp.backend.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.koder95.sbp.backend.factory.impl.MagicTokenFactoryImpl;
import pl.koder95.sbp.backend.security.ml.MagicToken;

public class MagicTokenFactoryTest {
    private static MagicTokenFactory magicTokenFactory;

    @BeforeAll
    static void setup() {
        magicTokenFactory = new MagicTokenFactoryImpl();
    }

    @Test
    void createMagicToken_nullsInput_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> magicTokenFactory.createMagicToken(null, null)
        );
    }

    @Test
    void createMagicToken_emptyString_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> magicTokenFactory.createMagicToken("", UUID.randomUUID())
        );
    }

    @Test
    void createMagicToken_validInputs_ok() {
        String expectedEmail = "student@example.com";
        UUID expectedNotificationUuid = UUID.randomUUID();
        MagicToken actual = magicTokenFactory
                .createMagicToken(expectedEmail, expectedNotificationUuid);
        assertNotNull(actual);
        assertEquals(expectedEmail, actual.email());
        assertNotNull(actual.createdAt());
        assertEquals(expectedNotificationUuid, actual.notificationUuid());
    }
}
