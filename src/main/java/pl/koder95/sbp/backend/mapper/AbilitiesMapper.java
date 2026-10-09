package pl.koder95.sbp.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import pl.koder95.sbp.backend.config.MapperConfig;
import pl.koder95.sbp.backend.dto.StudentAbilitiesDto;
import pl.koder95.sbp.backend.dto.UpdateStudentAbilitiesRequestDto;
import pl.koder95.sbp.backend.model.Abilities;

@Mapper(config = MapperConfig.class, uses = StudentMapper.class)
public interface AbilitiesMapper {
    StudentAbilitiesDto toResponseDto(Abilities abilities);

    void updateModel(@MappingTarget Abilities abilities,
                     UpdateStudentAbilitiesRequestDto requestDto);
}
