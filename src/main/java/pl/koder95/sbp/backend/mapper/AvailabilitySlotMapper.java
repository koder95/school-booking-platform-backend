package pl.koder95.sbp.backend.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.koder95.sbp.backend.config.MapperConfig;
import pl.koder95.sbp.backend.dto.AvailabilitySlotDto;
import pl.koder95.sbp.backend.dto.TeacherDtoWithoutEmail;
import pl.koder95.sbp.backend.exception.EntityNotFoundException;
import pl.koder95.sbp.backend.model.AvailabilitySlot;
import pl.koder95.sbp.backend.model.Teacher;
import pl.koder95.sbp.backend.repository.TeacherRepository;

@Mapper(config = MapperConfig.class, uses = TeacherMapper.class)
public interface AvailabilitySlotMapper {
    @Mapping(target = "teachers", source = "teachers")
    AvailabilitySlot toModel(AvailabilitySlotDto dto,
                             @Context TeacherRepository teacherRepository);

    @Mapping(target = "teachers", source = "teachers")
    AvailabilitySlotDto toDto(AvailabilitySlot model);

    default Teacher mapTeacher(TeacherDtoWithoutEmail teacher,
                               @Context TeacherRepository teacherRepository) {
        return teacherRepository.findById(teacher.uuid())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Teacher with uuid " + teacher.uuid() + " not found"
                ));
    }
}
