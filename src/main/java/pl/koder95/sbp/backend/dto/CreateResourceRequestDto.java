package pl.koder95.sbp.backend.dto;

import org.hibernate.validator.constraints.URL;
import pl.koder95.sbp.backend.model.Resource.Type;

public record CreateResourceRequestDto(
        @URL String url,
        Type type
) {
}
