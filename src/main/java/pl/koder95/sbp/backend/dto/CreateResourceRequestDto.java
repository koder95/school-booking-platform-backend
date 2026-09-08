package pl.koder95.sbp.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;
import pl.koder95.sbp.backend.model.Resource.Type;

@Schema(
        title = "Request to create a new resource",
        description = "Data transfer object for creating a new resource."
)
public record CreateResourceRequestDto(
        @Schema(
                title = "Resource URL",
                description = "The URL of the resource to be created.",
                example = "https://frontend.example.com/resources/avatar.png"
        )
        @NotNull @URL String url,
        @Schema(
                title = "Resource type",
                description = """
                The type of the resource to be created.
                It is used to categorize the resource
                and determine its significance within the system.
                """,
                examples = {
                        "AVATAR"
                }
        )
        @NotNull Type type
) {
}
