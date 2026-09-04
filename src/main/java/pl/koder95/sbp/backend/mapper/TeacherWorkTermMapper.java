package pl.koder95.sbp.backend.mapper;

import java.util.UUID;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.koder95.sbp.backend.config.MapperConfig;
import pl.koder95.sbp.backend.dto.TeacherDto;
import pl.koder95.sbp.backend.dto.TeacherDtoWithoutEmail;
import pl.koder95.sbp.backend.dto.UpdateTeacherRequestDto;
import pl.koder95.sbp.backend.model.TeacherWorkTerm;
import pl.koder95.sbp.backend.repository.TeacherRepository;
import pl.koder95.sbp.backend.repository.TeacherWorkTermRepository;

@Mapper(config = MapperConfig.class)
public interface TeacherWorkTermMapper {
    @Mapping(target = "teacher", expression = "java("
            + "teacherRepository.findById(teacherUuid)"
            + ".orElseThrow())")
    TeacherWorkTerm toModel(UUID teacherUuid, @Context TeacherRepository teacherRepository);

    @Mapping(target = "workType", source = "workType")
    @Mapping(target = "workDueDate", source = "workDueDate")
    void updateWorkTerm(@MappingTarget TeacherWorkTerm workTerm,
                        UpdateTeacherRequestDto requestDto);

    @Mapping(target = "workType", expression = "java("
            + "repository.findById(teacherDto.uuid())"
            + ".map(teacherWorkTerm -> teacherWorkTerm.getWorkType())"
            + ".orElse(null))")
    @Mapping(target = "workDueDate", expression = "java("
            + "repository.findById(teacherDto.uuid())"
            + ".map(teacherWorkTerm -> teacherWorkTerm.getWorkDueDate())"
            + ".orElse(null))")
    TeacherDto fetchWorkTerm(TeacherDto teacherDto,
                             @Context TeacherWorkTermRepository repository);

    @Mapping(target = "workType", expression = "java("
            + "repository.findById(teacherDto.uuid())"
            + ".map(teacherWorkTerm -> teacherWorkTerm.getWorkType())"
            + ".orElse(null))")
    @Mapping(target = "workDueDate", expression = "java("
            + "repository.findById(teacherDto.uuid())"
            + ".map(teacherWorkTerm -> teacherWorkTerm.getWorkDueDate())"
            + ".orElse(null))")
    TeacherDtoWithoutEmail fetchWorkTermWithoutEmail(
            TeacherDtoWithoutEmail teacherDto, @Context TeacherWorkTermRepository repository
    );
}
