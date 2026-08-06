package pl.koder95.sbp.backend.service.impl;

import java.time.Period;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.koder95.sbp.backend.dto.AvailabilityDto;
import pl.koder95.sbp.backend.dto.AvailabilitySlotDto;
import pl.koder95.sbp.backend.exception.EntityNotFoundException;
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
    private static final Logger logger = LoggerFactory.getLogger(AvailabilitySlotServiceImpl.class);

    private final AvailabilitySlotRepository repository;
    private final AvailabilitySlotMapper mapper;
    private final AvailabilityService availabilityService;
    private final TeacherRepository teacherRepository;
    private final ZonedDateTimeFactory timeFactory;
    private final Period period = Period.ofWeeks(1);

    @Override
    @Transactional
    public Page<AvailabilitySlotDto> createOrGetAll(Pageable pageable) {
        teacherRepository.findAll().forEach(
                teacher -> saveNonExistent(teacher,
                        timeFactory.createTimestampsWith(
                                teacher.getZoneId(),
                                period,
                                availabilityService.getFor(teacher.getUuid())
                        )
                )
        );
        return repository.findAllByTimestampAfter(ZonedDateTime.now(), pageable).map(mapper::toDto);
    }

    @Override
    @Transactional
    public void cleanOldAvailabilitySlots() {
        List<AvailabilitySlot> oldSlots = repository.findAllByTimestampBefore(ZonedDateTime.now());
        oldSlots.forEach(slot -> {
            slot.getTeachers().clear();
            repository.delete(slot);
        });
    }

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
        Set<AvailabilitySlot> availabilitySlots = teacher.getAvailabilitySlots();
        if (availabilitySlots != null) { // entity is persisted and has relations
            List<ZonedDateTime> existent = availabilitySlots.stream()
                    .map(AvailabilitySlot::getTimestamp)
                    .toList();
            timestamps = new ArrayList<>(timestamps);
            timestamps.removeAll(existent);
        }
        repository.saveAll(timestamps.stream().map(
                timestamp -> createOrGetAvailabilitySlot(timestamp).addTeacher(teacher)
        ).toList());
    }

    private AvailabilitySlot createOrGetAvailabilitySlot(ZonedDateTime timestamp) {
        if (repository.existsByTimestamp(timestamp)) {
            logger.info("Available slot localized: {}", timestamp);
            return repository.findByTimestamp(timestamp).orElseThrow();
        }
        logger.info("New available slot created: {}", timestamp);
        return new AvailabilitySlot().setTimestamp(timestamp);
    }

    @Override
    public List<AvailabilitySlotDto> getAllFor(UUID teacherUuid) {
        Teacher teacher = teacherRepository.findById(teacherUuid)
                .orElseThrow(() -> new EntityNotFoundException("cannot find a teacher"));
        return repository.findAllByTeacherAndTimestampAfter(teacher, ZonedDateTime.now()).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public Page<AvailabilitySlotDto> getAllFor(UUID teacherUuid, Pageable pageable) {
        Teacher teacher = teacherRepository.findById(teacherUuid)
                .orElseThrow(() -> new EntityNotFoundException("cannot find a teacher"));
        return repository.findAllByTeacherAndTimestampAfter(teacher, ZonedDateTime.now(), pageable)
                .map(mapper::toDto);
    }

    @Override
    public Page<AvailabilitySlotDto> getAll(Pageable pageable) {
        return repository.findAllByTimestampAfter(ZonedDateTime.now(), pageable)
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
