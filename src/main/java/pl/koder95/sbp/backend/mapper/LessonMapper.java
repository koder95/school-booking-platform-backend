package pl.koder95.sbp.backend.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.koder95.sbp.backend.config.MapperConfig;
import pl.koder95.sbp.backend.dto.CreateLessonRequestDto;
import pl.koder95.sbp.backend.dto.LessonDto;
import pl.koder95.sbp.backend.dto.UpdateLessonRequestDto;
import pl.koder95.sbp.backend.model.Lesson;
import pl.koder95.sbp.backend.repository.AvailabilitySlotRepository;
import pl.koder95.sbp.backend.repository.SubjectRepository;
import pl.koder95.sbp.backend.repository.TeacherRepository;

@Mapper(config = MapperConfig.class)
public interface LessonMapper {
    @Mapping(target = "uuid", expression = "java(availabilitySlotRepository"
            + ".findById(dto.availabilitySlotUuid()).orElseThrow().getUuid())")
    @Mapping(target = "assigned", expression = "java(teacherRepository"
            + ".findById(dto.teacherUuid()).orElseThrow())")
    @Mapping(target = "subject", expression = "java(subjectRepository"
            + ".findById(dto.subjectId()).orElseThrow())")
    @Mapping(target = "createdAt", expression = "java(java.time.ZonedDateTime.now())")
    @Mapping(target = "startTime", expression = "java(availabilitySlotRepository"
            + ".findById(dto.availabilitySlotUuid()).orElseThrow().getTimestamp())")
    @Mapping(target = "closingTime", expression = "java(availabilitySlotRepository"
            + ".findById(dto.availabilitySlotUuid()).orElseThrow().getTimestamp()"
            + ".minusMinutes(15))")
    Lesson toModel(CreateLessonRequestDto dto,
                   @Context AvailabilitySlotRepository availabilitySlotRepository,
                   @Context TeacherRepository teacherRepository,
                   @Context SubjectRepository subjectRepository);

    @Mapping(target = "lessonUuid", source = "uuid")
    @Mapping(target = "subjectId", source = "subject.id")
    @Mapping(target = "teacherUuid", source = "assigned.uuid")
    LessonDto toDto(Lesson model);

    @Mapping(target = "assigned", expression = "java(teacherRepository"
            + ".findById(dto.teacherUuid()).orElseThrow())")
    @Mapping(target = "subject", expression = "java(subjectRepository"
            + ".findById(dto.subjectId()).orElseThrow())")
    @Mapping(target = "maxEnrolled", expression = "java(dto.maxEnrolled())")
    void updateModel(@MappingTarget Lesson lesson, UpdateLessonRequestDto dto,
                     @Context TeacherRepository teacherRepository,
                     @Context SubjectRepository subjectRepository);
}
