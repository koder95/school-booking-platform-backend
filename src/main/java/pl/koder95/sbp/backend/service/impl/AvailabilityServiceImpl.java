package pl.koder95.sbp.backend.service.impl;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.koder95.sbp.backend.dto.AvailabilityDto;
import pl.koder95.sbp.backend.dto.UpdateAvailabilityRequestDto;
import pl.koder95.sbp.backend.mapper.AvailabilityMapper;
import pl.koder95.sbp.backend.model.Availability;
import pl.koder95.sbp.backend.model.Teacher;
import pl.koder95.sbp.backend.repository.AvailabilityRepository;
import pl.koder95.sbp.backend.repository.TeacherRepository;
import pl.koder95.sbp.backend.repository.TimeRangeRepository;
import pl.koder95.sbp.backend.service.AvailabilityService;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {
    private final AvailabilityRepository repository;
    private final AvailabilityMapper mapper;
    private final TeacherRepository teacherRepository;
    private final TimeRangeRepository timeRangeRepository;

    @Override
    public AvailabilityDto getFor(UUID teacherUuid) {
        Availability availability = repository.findById(teacherUuid).or(() -> {
            if (teacherRepository.existsById(teacherUuid)) {
                createEmptyFor(teacherUuid);
                return repository.findById(teacherUuid);
            } else {
                return Optional.empty();
            }
        }).orElseThrow();
        return mapper.toDto(availability);
    }

    @Override
    @Transactional
    public void createEmptyFor(UUID teacherUuid) {
        Teacher teacher = teacherRepository.findById(teacherUuid).orElseThrow();
        Availability availability = new Availability();
        availability.setTeacher(teacher);
        teacher.setAvailability(availability);
        repository.save(availability);
        teacherRepository.save(teacher);
    }

    @Override
    public AvailabilityDto updateFor(UUID teacherUuid, UpdateAvailabilityRequestDto dto,
                                     boolean patch) {
        Availability availability = repository.findById(teacherUuid).orElseThrow();
        if (patch) {
            mapper.addToModel(availability, dto, timeRangeRepository);
        } else {
            mapper.updateModel(availability, dto, timeRangeRepository);
        }
        Availability saved = repository.save(availability);
        return mapper.toDto(saved);
    }

    @Override
    public void deleteFor(UUID teacherUuid) {
        repository.deleteById(teacherUuid);
    }
}
