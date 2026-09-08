package pl.koder95.sbp.backend.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.koder95.sbp.backend.dto.CreateResourceRequestDto;
import pl.koder95.sbp.backend.dto.ResourceDto;
import pl.koder95.sbp.backend.exception.EntityNotFoundException;
import pl.koder95.sbp.backend.mapper.ResourceMapper;
import pl.koder95.sbp.backend.model.Resource;
import pl.koder95.sbp.backend.model.Resource.Type;
import pl.koder95.sbp.backend.repository.ResourceRepository;
import pl.koder95.sbp.backend.service.ResourceService;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository repository;
    private final ResourceMapper mapper;

    @Override
    public ResourceDto create(CreateResourceRequestDto requestDto) {
        return null;
    }

    @Override
    public ResourceDto delete(UUID uuid) {
        Resource toDelete = repository.findById(uuid).orElseThrow(
                () -> new EntityNotFoundException("Resource with uuid: " + uuid + " not found")
        );
        return mapper.toDto(toDelete);
    }

    @Override
    public ResourceDto get(UUID uuid) {
        return repository.findById(uuid)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Resource with uuid: " + uuid + " not found"
                ));
    }

    @Override
    public ResourceDto get(String url) {
        return repository.findByUrl(url)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Resource with url: " + url + " not found"
                ));
    }

    @Override
    public Page<ResourceDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public Page<ResourceDto> getAllByType(Type type, Pageable pageable) {
        return repository.findAllByType(type, pageable).map(mapper::toDto);
    }
}
