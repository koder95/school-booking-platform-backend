package pl.koder95.sbp.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import pl.koder95.sbp.backend.config.MapperConfig;
import pl.koder95.sbp.backend.dto.CreateSubjectRequestDto;
import pl.koder95.sbp.backend.dto.SubjectDto;
import pl.koder95.sbp.backend.dto.UpdateSubjectRequestDto;
import pl.koder95.sbp.backend.model.Subject;

@Mapper(config = MapperConfig.class)
public interface SubjectMapper {
    Subject toModel(CreateSubjectRequestDto requestDto);

    SubjectDto toResponseDto(Subject model);

    void updateModel(@MappingTarget Subject subject, UpdateSubjectRequestDto requestDto);
}
