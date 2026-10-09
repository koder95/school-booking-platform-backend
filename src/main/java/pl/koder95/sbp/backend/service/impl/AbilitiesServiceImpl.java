package pl.koder95.sbp.backend.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.koder95.sbp.backend.dto.StudentAbilitiesDto;
import pl.koder95.sbp.backend.dto.UpdateStudentAbilitiesRequestDto;
import pl.koder95.sbp.backend.exception.EntityNotFoundException;
import pl.koder95.sbp.backend.mapper.AbilitiesMapper;
import pl.koder95.sbp.backend.model.Abilities;
import pl.koder95.sbp.backend.model.Student;
import pl.koder95.sbp.backend.repository.AbilitiesRepository;
import pl.koder95.sbp.backend.repository.StudentRepository;
import pl.koder95.sbp.backend.service.AbilitiesService;

@Service
@RequiredArgsConstructor
public class AbilitiesServiceImpl implements AbilitiesService {
    private final AbilitiesRepository repository;
    private final AbilitiesMapper mapper;
    private final StudentRepository studentRepository;

    @Override
    public StudentAbilitiesDto getAbilities(UUID studentUuid) {
        return mapper.toResponseDto(createOrGetAbilities(studentUuid));
    }

    @Override
    @Transactional
    public StudentAbilitiesDto updateAbilities(UUID studentUuid,
                                               UpdateStudentAbilitiesRequestDto requestDto) {
        Abilities abilities = createOrGetAbilities(studentUuid);
        mapper.updateModel(abilities, requestDto);
        return mapper.toResponseDto(abilities);
    }

    public Abilities createOrGetAbilities(UUID studentUuid) {
        Student student = studentRepository.findById(studentUuid)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cannot find a student: " + studentUuid
                ));
        return repository.findById(studentUuid).orElseGet(() -> {
            Abilities newAbilities = new Abilities();
            newAbilities.setStudent(student);
            newAbilities.setRemainingLessons(1);
            return repository.save(newAbilities);
        });
    }
}
