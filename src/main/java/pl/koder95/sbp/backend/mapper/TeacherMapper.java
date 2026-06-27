package pl.koder95.sbp.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.koder95.sbp.backend.config.MapperConfig;
import pl.koder95.sbp.backend.dto.CreateTeacherRequestDto;
import pl.koder95.sbp.backend.dto.TeacherDto;
import pl.koder95.sbp.backend.dto.TeacherDtoWithoutEmail;
import pl.koder95.sbp.backend.dto.UpdateTeacherRequestDto;
import pl.koder95.sbp.backend.model.Teacher;

@Mapper(config = MapperConfig.class)
public interface TeacherMapper {
    @Mapping(target = "email", source = "email", ignore = true)
    Teacher toModel(CreateTeacherRequestDto dto);

    @Mapping(target = "emailId", source = "email.id")
    TeacherDto toResponseDto(Teacher model);

    TeacherDtoWithoutEmail toResponseDtoWithoutEmail(Teacher model);

    @Mapping(target = "email", source = "email", ignore = true)
    void updateModel(@MappingTarget Teacher model, UpdateTeacherRequestDto dto);
}
