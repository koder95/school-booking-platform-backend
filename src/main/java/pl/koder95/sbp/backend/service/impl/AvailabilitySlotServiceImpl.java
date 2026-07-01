package pl.koder95.sbp.backend.service.impl;

import java.time.Period;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.koder95.sbp.backend.dto.AvailabilityDto;
import pl.koder95.sbp.backend.dto.AvailabilitySlotDto;
import pl.koder95.sbp.backend.factory.ZonedDateTimeFactory;
import pl.koder95.sbp.backend.mapper.AvailabilitySlotMapper;
import pl.koder95.sbp.backend.model.AvailabilitySlot;
import pl.koder95.sbp.backend.model.Teacher;
import pl.koder95.sbp.backend.repository.AvailabilitySlotRepository;
import pl.koder95.sbp.backend.repository.TeacherRepository;
import pl.koder95.sbp.backend.service.AvailabilityService;
import pl.koder95.sbp.backend.service.AvailabilitySlotService;

@Service
@RequiredArgsConstructor
public class AvailabilitySlotServiceImpl implements AvailabilitySlotService {
    private final AvailabilitySlotRepository repository;
    private final AvailabilitySlotMapper mapper;
    private final AvailabilityService availabilityService;
    private final TeacherRepository teacherRepository;
    private final ZonedDateTimeFactory timeFactory;
    private final Period period = Period.ofWeeks(1);

    @Override
    @Transactional
    public List<AvailabilitySlotDto> createOrGetFor(
            UUID teacherUuid, ZonedDateTime startTime, ZonedDateTime endTime
    ) {
        AvailabilityDto dto = availabilityService.getFor(teacherUuid);
        Teacher teacher = teacherRepository.findById(teacherUuid).orElseThrow();
        saveNonExistent(teacher, timeFactory
                .createTimestampsWith(teacher.getZoneId(), period, dto));
        return repository.findAllByTeacherAndTimestampBetween(teacher, startTime, endTime).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public Page<AvailabilitySlotDto> createOrGetFor(
            UUID teacherUuid, ZonedDateTime start, ZonedDateTime end, Pageable pageable
    ) {
        AvailabilityDto dto = availabilityService.getFor(teacherUuid);
        Teacher teacher = teacherRepository.findById(teacherUuid).orElseThrow();
        saveNonExistent(teacher, timeFactory
                .createTimestampsWith(teacher.getZoneId(), period, dto));
        return repository.findAllByTeacherAndTimestampBetween(teacher, start, end, pageable)
                .map(mapper::toDto);
    }

    private void saveNonExistent(Teacher teacher, List<ZonedDateTime> timestamps) {
        List<ZonedDateTime> existent = teacher.getAvailabilitySlots().stream()
                .map(AvailabilitySlot::getTimestamp)
                .toList();
        timestamps = new ArrayList<>(timestamps);
        timestamps.removeAll(existent);
        timestamps = List.copyOf(timestamps);
        repository.saveAll(timestamps.stream().map(
                timestamp -> createOrGetAvailabilitySlot(timestamp).addTeacher(teacher)
        ).toList());
    }

    private AvailabilitySlot createOrGetAvailabilitySlot(ZonedDateTime timestamp) {
        if (repository.existsByTimestamp(timestamp)) {
            return repository.findByTimestamp(timestamp).orElseThrow();
        }
        return new AvailabilitySlot().setTimestamp(timestamp);
    }

    @Override
    public List<AvailabilitySlotDto> getAllFor(UUID teacherUuid) {
        Teacher teacher = teacherRepository.findById(teacherUuid).orElseThrow();
        return repository.findAllByTeacher(teacher).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public Page<AvailabilitySlotDto> getAllFor(UUID teacherUuid, Pageable pageable) {
        Teacher teacher = teacherRepository.findById(teacherUuid).orElseThrow();
        return repository.findAllByTeacher(teacher, pageable)
                .map(mapper::toDto);
    }

    @Override
    @Transactional
    public List<AvailabilitySlotDto> deleteAllFor(UUID teacherUuid) {
        Teacher teacher = teacherRepository.findById(teacherUuid).orElseThrow();
        List<AvailabilitySlot> slots = repository.findAllByTeacher(teacher);
        slots.forEach(slot -> repository.save(slot.removeTeacher(teacher)));
        return slots.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Page<AvailabilitySlotDto> deleteAllFor(UUID teacherUuid, Pageable pageable) {
        Teacher teacher = teacherRepository.findById(teacherUuid).orElseThrow();
        Page<AvailabilitySlot> slots = repository.findAllByTeacher(teacher, pageable);
        slots.forEach(slot -> repository.save(slot.removeTeacher(teacher)));
        return slots.map(mapper::toDto);
    }
}
