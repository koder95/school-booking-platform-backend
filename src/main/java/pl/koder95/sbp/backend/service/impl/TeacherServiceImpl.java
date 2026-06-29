package pl.koder95.sbp.backend.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.koder95.sbp.backend.dto.CreateTeacherRequestDto;
import pl.koder95.sbp.backend.dto.TeacherDto;
import pl.koder95.sbp.backend.dto.TeacherDtoWithoutEmail;
import pl.koder95.sbp.backend.dto.UpdateTeacherRequestDto;
import pl.koder95.sbp.backend.mapper.TeacherMapper;
import pl.koder95.sbp.backend.model.Email;
import pl.koder95.sbp.backend.model.Teacher;
import pl.koder95.sbp.backend.repository.EmailRepository;
import pl.koder95.sbp.backend.repository.TeacherRepository;
import pl.koder95.sbp.backend.service.AvailabilityService;
import pl.koder95.sbp.backend.service.TeacherService;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository repository;
    private final TeacherMapper mapper;
    private final AvailabilityService availabilityService;
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
    public Page<TeacherDtoWithoutEmail> getAllWithoutEmails(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDtoWithoutEmail);
    }

    @Override
    @Transactional
    public TeacherDto create(CreateTeacherRequestDto requestDto) {
        Teacher model = mapper.toModel(requestDto, emailRepository);
        TeacherDto responseDto = mapper.toResponseDto(repository.save(model));
        availabilityService.createEmptyFor(responseDto.uuid());
        return responseDto;
    }

    @Override
    @Transactional
    public TeacherDto update(UUID uuid, UpdateTeacherRequestDto requestDto) {
        Teacher model = repository.findById(uuid).orElseThrow();
        if (requestDto.email() != null) {
            updateEmail(model, requestDto.email());
        }
        model = repository.save(model);
        return mapper.toResponseDto(model);
    }

    private void updateEmail(Teacher model, String email) {
        model.setEmail(emailRepository.findByValue(email).orElseGet(
                () -> emailRepository.save(new Email().setValue(email))
        ));
    }

    @Override
    @Transactional
    public TeacherDto delete(UUID uuid) {
        Teacher teacher = repository.findById(uuid).orElseThrow();
        availabilityService.deleteFor(uuid);
        repository.delete(teacher);
        return mapper.toResponseDto(teacher);
    }
}
