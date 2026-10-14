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
import pl.koder95.sbp.backend.dto.UpdateTeacherAvatarRequestDto;
import pl.koder95.sbp.backend.dto.UpdateTeacherRequestDto;
import pl.koder95.sbp.backend.exception.EntityNotFoundException;
import pl.koder95.sbp.backend.mapper.TeacherColorMapper;
import pl.koder95.sbp.backend.mapper.TeacherMapper;
import pl.koder95.sbp.backend.mapper.TeacherWorkTermMapper;
import pl.koder95.sbp.backend.model.Email;
import pl.koder95.sbp.backend.model.Teacher;
import pl.koder95.sbp.backend.model.TeacherColor;
import pl.koder95.sbp.backend.model.TeacherWorkTerm;
import pl.koder95.sbp.backend.repository.EmailRepository;
import pl.koder95.sbp.backend.repository.ResourceRepository;
import pl.koder95.sbp.backend.repository.SubjectRepository;
import pl.koder95.sbp.backend.repository.TeacherColorRepository;
import pl.koder95.sbp.backend.repository.TeacherRepository;
import pl.koder95.sbp.backend.repository.TeacherWorkTermRepository;
import pl.koder95.sbp.backend.service.AvailabilityService;
import pl.koder95.sbp.backend.service.TeacherService;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository repository;
    private final TeacherMapper mapper;
    private final AvailabilityService availabilityService;
    private final EmailRepository emailRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherColorRepository colorRepository;
    private final TeacherColorMapper colorMapper;
    private final TeacherWorkTermRepository workTermRepository;
    private final TeacherWorkTermMapper workTermMapper;
    private final ResourceRepository resourceRepository;

    @Override
    public TeacherDto get(UUID uuid) {
        return repository.findById(uuid)
                .map(mapper::toResponseDto)
                .map(this::fetchAll)
                .orElseThrow(() -> new EntityNotFoundException("teacher not found: " + uuid));
    }

    @Override
    public Page<TeacherDto> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponseDto)
                .map(this::fetchAll);
    }

    @Override
    public Page<TeacherDtoWithoutEmail> getAllWithoutEmails(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponseDtoWithoutEmail)
                .map(responseDto -> colorMapper
                        .fetchColorAndWithoutEmail(responseDto, colorRepository))
                .map(responseDto -> workTermMapper
                        .fetchWorkTermWithoutEmail(responseDto, workTermRepository));
    }

    @Override
    @Transactional
    public TeacherDto create(CreateTeacherRequestDto requestDto) {
        Teacher model = mapper.toModel(requestDto, emailRepository, subjectRepository);
        repository.save(model);
        TeacherColor colorModel = colorMapper.toModel(model.getUuid(), repository);
        colorMapper.updateModel(colorModel, requestDto.color());
        colorRepository.save(colorModel);
        availabilityService.createEmptyFor(model.getUuid());
        TeacherWorkTerm workTerm = new TeacherWorkTerm();
        workTerm.setTeacher(model);
        workTermRepository.save(workTerm);
        return fetchAll(mapper.toResponseDto(model));
    }

    @Override
    @Transactional
    public TeacherDto update(UUID uuid, UpdateTeacherRequestDto requestDto) {
        Teacher model = repository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("teacher does not exist"));
        if (requestDto.email() != null) {
            updateEmail(model, requestDto.email());
        }
        mapper.updateModel(model, requestDto,
                emailRepository, subjectRepository, resourceRepository);
        model = repository.save(model);
        TeacherDto result = mapper.toResponseDto(model);
        if (requestDto.color() != null) {
            TeacherColor colorModel = colorRepository.findById(uuid)
                    .orElse(colorMapper.toModel(uuid, repository));
            colorMapper.updateModel(colorModel, requestDto.color());
            colorRepository.save(colorModel);
            result = colorMapper.fetchColor(result, colorRepository);
        }
        if (requestDto.workType() != null || requestDto.workDueDate() != null) {
            TeacherWorkTerm workTerm = workTermRepository.findById(uuid)
                    .orElse(workTermMapper.toModel(uuid, repository));
            workTermMapper.updateWorkTerm(workTerm, requestDto);
            workTermRepository.save(workTerm);
            result = workTermMapper.fetchWorkTerm(result, workTermRepository);
        }
        return fetchAll(mapper.toResponseDto(model));
    }

    @Override
    @Transactional
    public TeacherDto update(UUID uuid, UpdateTeacherAvatarRequestDto requestDto) {
        Teacher model = repository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("teacher does not exist"));
        model.setAvatar(resourceRepository.saveAvatarUrl(requestDto.avatarUrl()));
        return fetchAll(mapper.toResponseDto(model));
    }

    private TeacherDto fetchAll(TeacherDto dto) {
        TeacherDto result = colorMapper.fetchColor(dto, colorRepository);
        result = workTermMapper.fetchWorkTerm(result, workTermRepository);
        return result;
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
        teacher.setAvatar(null);
        availabilityService.deleteFor(uuid);
        TeacherWorkTerm workTerm = workTermRepository.findByTeacher(teacher);
        workTermRepository.delete(workTerm);
        TeacherColor colorModel = colorMapper.toModel(uuid, repository);
        TeacherDto teacherDto = colorMapper.fetchColor(
                mapper.toResponseDto(teacher), colorRepository
        );
        colorRepository.delete(colorModel);
        repository.delete(teacher);
        return teacherDto;
    }
}
