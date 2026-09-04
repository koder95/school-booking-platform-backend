package pl.koder95.sbp.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.koder95.sbp.backend.dto.ResourceDto;
import pl.koder95.sbp.backend.service.ResourceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resources")
@Tag(name = "Resources", description = "Operations related to resources")
public class ResourceController {
    private final ResourceService resourceService;

    @GetMapping
    @Operation(summary = "Get all resources", description = "Get all resources with pagination")
    public Page<ResourceDto> getAllResources(@ParameterObject Pageable pageable) {
        return resourceService.getAll(pageable);
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get resource by UUID",
            description = "Get a specific resource by its UUID")
    public ResourceDto getResourceByUuid(@PathVariable String uuid) {
        return resourceService.get(UUID.fromString(uuid));
    }
}
