package pl.koder95.sbp.backend.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.koder95.sbp.backend.dto.CreateStudentRequestDto;
import pl.koder95.sbp.backend.dto.StudentDto;
import pl.koder95.sbp.backend.dto.UpdateStudentRequestDto;
import pl.koder95.sbp.backend.exception.EntityNotFoundException;
import pl.koder95.sbp.backend.mapper.StudentMapper;
import pl.koder95.sbp.backend.model.Email;
import pl.koder95.sbp.backend.model.Student;
import pl.koder95.sbp.backend.repository.EmailRepository;
import pl.koder95.sbp.backend.repository.StudentRepository;
import pl.koder95.sbp.backend.service.StudentService;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository repository;
    private final StudentMapper mapper;
    private final EmailRepository emailRepository;

    @Override
    public Page<StudentDto> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(student -> mapper.toDto(student, emailRepository));
    }

    @Override
    public StudentDto get(UUID studentUuid) {
        return repository.findById(studentUuid)
                .map(student -> mapper.toDto(student, emailRepository))
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));
    }

    @Override
    public StudentDto create(CreateStudentRequestDto dto) {
        if (emailRepository.findByValue(dto.email()).isEmpty()) {
            emailRepository.save(new Email().setValue(dto.email()));
        }
        Student student = mapper.toModel(dto, emailRepository);
        return mapper.toDto(repository.save(student), emailRepository);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Transactional
    @Override
    public StudentDto update(UUID studentUuid, UpdateStudentRequestDto requestDto) {
        Student student = repository.findById(studentUuid)
                .orElseThrow(
                        () -> new EntityNotFoundException("student doesn't exist: " + studentUuid)
                );
        mapper.updateModel(student, requestDto);
        return mapper.toDto(student, emailRepository);
    }
}
