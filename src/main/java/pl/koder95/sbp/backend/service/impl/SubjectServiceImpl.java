package pl.koder95.sbp.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.koder95.sbp.backend.dto.CreateSubjectRequestDto;
import pl.koder95.sbp.backend.dto.SubjectDto;
import pl.koder95.sbp.backend.dto.UpdateSubjectRequestDto;
import pl.koder95.sbp.backend.mapper.SubjectMapper;
import pl.koder95.sbp.backend.model.Subject;
import pl.koder95.sbp.backend.repository.SubjectRepository;
import pl.koder95.sbp.backend.service.SubjectService;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository repository;
    private final SubjectMapper mapper;

    @Override
    public SubjectDto create(CreateSubjectRequestDto requestDto) {
        Subject model = mapper.toModel(requestDto);
        return mapper.toResponseDto(repository.save(model));
    }

    @Override
    public SubjectDto get(Long id) {
        return repository.findById(id).map(mapper::toResponseDto).orElseThrow();
    }

    @Override
    public SubjectDto getByName(String name) {
        return repository.findByName(name).map(mapper::toResponseDto).orElseThrow();
    }

    @Override
    public SubjectDto update(Long id, UpdateSubjectRequestDto requestDto) {
        Subject subject = repository.findById(id).orElseThrow();
        mapper.updateModel(subject, requestDto);
        return mapper.toResponseDto(repository.save(subject));
    }

    @Override
    public SubjectDto delete(Long id) {
        Subject subject = repository.findById(id).orElseThrow();
        repository.delete(subject);
        return mapper.toResponseDto(subject);
    }

    @Override
    public Page<SubjectDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDto);
    }
}
