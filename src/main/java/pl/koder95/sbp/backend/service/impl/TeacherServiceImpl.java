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
import pl.koder95.sbp.backend.exception.EntityNotFoundException;
import pl.koder95.sbp.backend.mapper.TeacherColorMapper;
import pl.koder95.sbp.backend.mapper.TeacherMapper;
import pl.koder95.sbp.backend.mapper.TeacherWorkTermMapper;
import pl.koder95.sbp.backend.model.Email;
import pl.koder95.sbp.backend.model.Teacher;
import pl.koder95.sbp.backend.model.TeacherColor;
import pl.koder95.sbp.backend.model.TeacherWorkTerm;
import pl.koder95.sbp.backend.repository.EmailRepository;
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

    @Override
    public TeacherDto get(UUID uuid) {
        return repository.findById(uuid)
                .map(mapper::toResponseDto)
                .map(responseDto -> colorMapper.fetchColor(responseDto, colorRepository))
                .orElseThrow(() -> new EntityNotFoundException("teacher not found: " + uuid));
    }

    @Override
    public Page<TeacherDto> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponseDto)
                .map(responseDto -> colorMapper.fetchColor(responseDto, colorRepository));
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
        TeacherDto responseDto = mapper.toResponseDto(repository.save(model));
        TeacherColor colorModel = colorMapper.toModel(model.getUuid(), repository);
        colorMapper.updateModel(colorModel, requestDto.color());
        colorRepository.save(colorModel);
        availabilityService.createEmptyFor(responseDto.uuid());
        TeacherWorkTerm workTerm = new TeacherWorkTerm();
        workTerm.setTeacher(model);
        workTermRepository.save(workTerm);
        responseDto = colorMapper.fetchColor(responseDto, colorRepository);
        return workTermMapper.fetchWorkTerm(responseDto, workTermRepository);
    }

    @Override
    @Transactional
    public TeacherDto update(UUID uuid, UpdateTeacherRequestDto requestDto) {
        Teacher model = repository.findById(uuid).orElseThrow();
        if (requestDto.email() != null) {
            updateEmail(model, requestDto.email());
        }
        mapper.updateModel(model, requestDto, emailRepository, subjectRepository);
        model = repository.save(model);
        if (requestDto.color() == null) {
            return mapper.toResponseDto(model);
        }
        TeacherColor colorModel = colorMapper.toModel(model.getUuid(), repository);
        colorMapper.updateModel(colorModel, requestDto.color());
        colorRepository.save(colorModel);
        if (requestDto.workType() != null || requestDto.workDueDate() != null) {
            TeacherWorkTerm workTerm = workTermRepository.findByTeacher(model);
            workTermMapper.updateWorkTerm(workTerm, requestDto);
            workTermRepository.save(workTerm);
        }
        return colorMapper.fetchColor(mapper.toResponseDto(model), colorRepository);
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
