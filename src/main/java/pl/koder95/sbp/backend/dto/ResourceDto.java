package pl.koder95.sbp.backend.dto;

import java.util.UUID;
import pl.koder95.sbp.backend.model.Resource.Type;

public record ResourceDto(
        UUID uuid,
        String url,
        Type type
) {
}
