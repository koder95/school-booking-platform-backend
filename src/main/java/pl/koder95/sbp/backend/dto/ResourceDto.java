package pl.koder95.sbp.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import pl.koder95.sbp.backend.model.Resource.Type;

@Schema(
        title = "Response with a resource",
        description = "Data transfer object representing a resource."
)
public record ResourceDto(
        @Schema(
                title = "Resource UUID",
                description = "The unique identifier of the resource.",
                example = "123e4567-e89b-12d3-a456-426614174000"
        )
        UUID uuid,
        @Schema(
                title = "Resource URL",
                description = "The URL of the resource.",
                example = "https://frontend.example.com/resources/avatar.png"
        )
        String url,
        @Schema(
                title = "Resource type",
                description = "The type of the resource.",
                examples = {
                        "AVATAR"
                }
        )
        Type type
) {
}
