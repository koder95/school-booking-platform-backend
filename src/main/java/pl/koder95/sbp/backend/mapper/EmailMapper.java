package pl.koder95.sbp.backend.mapper;

import org.mapstruct.Mapper;
import pl.koder95.sbp.backend.config.MapperConfig;
import pl.koder95.sbp.backend.dto.EmailDto;
import pl.koder95.sbp.backend.dto.EmailValueDto;
import pl.koder95.sbp.backend.model.Email;

@Mapper(config = MapperConfig.class)
public interface EmailMapper {
    EmailDto toResponseDto(Email email);

    Email toModel(EmailDto emailDto);

    Email toModel(EmailValueDto emailValueDto);
}
