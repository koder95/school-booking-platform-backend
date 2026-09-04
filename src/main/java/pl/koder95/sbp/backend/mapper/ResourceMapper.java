package pl.koder95.sbp.backend.mapper;

import org.mapstruct.Mapper;
import pl.koder95.sbp.backend.config.MapperConfig;
import pl.koder95.sbp.backend.dto.ResourceDto;
import pl.koder95.sbp.backend.model.Resource;

@Mapper(config = MapperConfig.class)
public interface ResourceMapper {
    ResourceDto toDto(Resource model);
}
