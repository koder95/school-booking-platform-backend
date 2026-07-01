package pl.koder95.sbp.backend.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.koder95.sbp.backend.config.MapperConfig;
import pl.koder95.sbp.backend.dto.CreateTeacherRequestDto;
import pl.koder95.sbp.backend.dto.TeacherDto;
import pl.koder95.sbp.backend.dto.TeacherDtoWithoutEmail;
import pl.koder95.sbp.backend.dto.UpdateTeacherRequestDto;
import pl.koder95.sbp.backend.model.Email;
import pl.koder95.sbp.backend.model.Teacher;
import pl.koder95.sbp.backend.repository.EmailRepository;

@Mapper(config = MapperConfig.class)
public interface TeacherMapper {
    @Mapping(target = "email", source = "email")
    Teacher toModel(CreateTeacherRequestDto dto, @Context EmailRepository repository);

    @Mapping(target = "emailId", source = "email.id")
    TeacherDto toResponseDto(Teacher model);

    TeacherDtoWithoutEmail toResponseDtoWithoutEmail(Teacher model);

    @Mapping(target = "email", source = "email")
    void updateModel(@MappingTarget Teacher model, UpdateTeacherRequestDto dto,
                     @Context EmailRepository repository);

    default Email mapEmail(String email, @Context EmailRepository repository) {
        return repository.findByValue(email).orElseGet(
                () -> repository.save(new Email().setValue(email))
        );
    }
}
