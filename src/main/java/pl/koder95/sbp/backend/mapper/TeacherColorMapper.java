package pl.koder95.sbp.backend.mapper;

import java.util.UUID;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.koder95.sbp.backend.config.MapperConfig;
import pl.koder95.sbp.backend.dto.TeacherDto;
import pl.koder95.sbp.backend.dto.TeacherDtoWithoutEmail;
import pl.koder95.sbp.backend.model.TeacherColor;
import pl.koder95.sbp.backend.repository.TeacherColorRepository;
import pl.koder95.sbp.backend.repository.TeacherRepository;

@Mapper(config = MapperConfig.class)
public interface TeacherColorMapper {
    @Mapping(target = "color", expression = "java("
            + "repository.findById(teacherDto.uuid())"
            + ".map(teacherColor -> teacherColor.getColorHex())"
            + ".orElse(null))")
    TeacherDto fetchColor(TeacherDto teacherDto, @Context TeacherColorRepository repository);

    @Mapping(target = "color", expression = "java("
            + "repository.findById(teacherDto.uuid())"
            + ".map(teacherColor -> teacherColor.getColorHex())"
            + ".orElse(null))")
    TeacherDtoWithoutEmail fetchColorAndWithoutEmail(TeacherDtoWithoutEmail teacherDto,
                                                     TeacherColorRepository repository);

    @Mapping(target = "teacher", expression = "java("
            + "teacherRepository.findById(teacherUuid)"
            + ".orElseThrow())")
    TeacherColor toModel(UUID teacherUuid, @Context TeacherRepository teacherRepository);

    @Mapping(target = "colorHex", expression = "java(color)")
    void updateModel(@MappingTarget TeacherColor model, String color);
}
