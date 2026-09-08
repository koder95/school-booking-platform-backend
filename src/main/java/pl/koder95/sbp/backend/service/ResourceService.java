package pl.koder95.sbp.backend.service;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.koder95.sbp.backend.dto.CreateResourceRequestDto;
import pl.koder95.sbp.backend.dto.ResourceDto;
import pl.koder95.sbp.backend.model.Resource.Type;

public interface ResourceService {
    ResourceDto create(CreateResourceRequestDto requestDto);

    ResourceDto delete(UUID uuid);

    ResourceDto get(UUID uuid);

    ResourceDto get(String url);

    Page<ResourceDto> getAll(Pageable pageable);

    Page<ResourceDto> getAllByType(Type type, Pageable pageable);
}
