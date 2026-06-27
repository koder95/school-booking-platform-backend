package pl.koder95.sbp.backend.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.koder95.sbp.backend.dto.CreateTeacherRequestDto;
import pl.koder95.sbp.backend.dto.EmailValueDto;
import pl.koder95.sbp.backend.dto.TeacherDto;
import pl.koder95.sbp.backend.dto.TeacherDtoWithoutEmail;
import pl.koder95.sbp.backend.dto.UpdateTeacherRequestDto;
import pl.koder95.sbp.backend.exception.EmailAlreadyExistsException;
import pl.koder95.sbp.backend.exception.EntityNotFoundException;
import pl.koder95.sbp.backend.exception.InvalidEmailValueException;
import pl.koder95.sbp.backend.mapper.EmailMapper;
import pl.koder95.sbp.backend.mapper.TeacherMapper;
import pl.koder95.sbp.backend.model.Email;
import pl.koder95.sbp.backend.model.Teacher;
import pl.koder95.sbp.backend.repository.TeacherRepository;
import pl.koder95.sbp.backend.service.EmailService;
import pl.koder95.sbp.backend.service.TeacherService;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository repository;
    private final TeacherMapper mapper;
    private final EmailMapper emailMapper;
    private final EmailService emailService;

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
        Teacher model = mapper.toModel(requestDto);
        updateEmail(requestDto.email(), model);
        TeacherDto responseDto = mapper.toResponseDto(repository.save(model));
        return responseDto;
    }

    @Override
    public TeacherDto update(UUID uuid, UpdateTeacherRequestDto requestDto) {
        Teacher model = repository.findById(uuid).orElseThrow();
        mapper.updateModel(model, requestDto);
        if (requestDto.email() != null) {
            updateEmail(requestDto.email(), model);
        }
        model = repository.save(model);
        return mapper.toResponseDto(model);
    }

    private void updateEmail(String requestDto, Teacher model) {
        Email email;
        try {
            email = emailMapper.toModel(emailService.findByValue(requestDto));
        } catch (EntityNotFoundException e) {
            try {
                email = emailMapper.toModel(emailService.register(new EmailValueDto(requestDto)));
            } catch (EmailAlreadyExistsException | InvalidEmailValueException ex) {
                throw new RuntimeException(ex);
            }
        }
        model.setEmail(email);
    }

    @Override
    public TeacherDto delete(UUID uuid) {
        Teacher teacher = repository.findById(uuid).orElseThrow();
        repository.delete(teacher);
        return mapper.toResponseDto(teacher);
    }
}
