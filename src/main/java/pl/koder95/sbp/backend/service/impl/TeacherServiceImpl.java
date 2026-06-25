package pl.koder95.sbp.backend.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.koder95.sbp.backend.dto.CreateTeacherRequestDto;
import pl.koder95.sbp.backend.dto.TeacherDto;
import pl.koder95.sbp.backend.dto.UpdateTeacherRequestDto;
import pl.koder95.sbp.backend.mapper.TeacherMapper;
import pl.koder95.sbp.backend.model.Email;
import pl.koder95.sbp.backend.model.Teacher;
import pl.koder95.sbp.backend.repository.EmailRepository;
import pl.koder95.sbp.backend.repository.TeacherRepository;
import pl.koder95.sbp.backend.service.TeacherService;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository repository;
    private final TeacherMapper mapper;
    private final EmailRepository emailRepository;

    @Override
    public TeacherDto get(UUID uuid) {
        return repository.findById(uuid).map(mapper::toResponseDto).orElseThrow();
    }

    @Override
    public Page<TeacherDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDto);
    }

    @Override
    public TeacherDto create(CreateTeacherRequestDto requestDto) {
        Teacher model = mapper.toModel(requestDto);
        if (requestDto.email() != null) {
            Email email = emailRepository.findByValue(requestDto.email()).orElseGet(() -> {
                Email created = new Email();
                created.setValue(requestDto.email());
                return emailRepository.save(created);
            });
            model.setEmail(email);
        }
        return mapper.toResponseDto(repository.save(model));
    }

    @Override
    public TeacherDto update(UUID uuid, UpdateTeacherRequestDto requestDto) {
        Teacher model = repository.findById(uuid).orElseThrow();
        mapper.updateModel(model, requestDto);
        if (requestDto.email() != null) {
            Email email = emailRepository.findByValue(requestDto.email()).orElseGet(() -> {
                Email created = new Email();
                created.setValue(requestDto.email());
                return emailRepository.save(created);
            });
            model.setEmail(email);
        }
        model = repository.save(model);
        return mapper.toResponseDto(model);
    }

    @Override
    public TeacherDto delete(UUID uuid) {
        Teacher teacher = repository.findById(uuid).orElseThrow();
        repository.delete(teacher);
        return mapper.toResponseDto(teacher);
    }
}
