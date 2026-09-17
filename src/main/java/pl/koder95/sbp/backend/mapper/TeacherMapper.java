package pl.koder95.sbp.backend.mapper;

import org.mapstruct.AfterMapping;
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
import pl.koder95.sbp.backend.repository.ResourceRepository;
import pl.koder95.sbp.backend.repository.SubjectRepository;
import pl.koder95.sbp.backend.repository.TeacherColorRepository;

@Mapper(config = MapperConfig.class, uses = {TeacherColorMapper.class})
public interface TeacherMapper {
    @Mapping(target = "email", source = "email")
    @Mapping(target = "subject",
            expression = "java(subjectRepository.findById(dto.subjectId()).orElseThrow())")
    Teacher toModel(CreateTeacherRequestDto dto,
                    @Context EmailRepository repository,
                    @Context SubjectRepository subjectRepository);

    @Mapping(target = "email", source = "email.value")
    @Mapping(target = "avatarUrl", source = "avatar.url")
    TeacherDto toResponseDto(Teacher model);

    @Mapping(target = "avatarUrl", source = "avatar.url")
    TeacherDtoWithoutEmail toResponseDtoWithoutEmail(Teacher model);

    @Mapping(target = "email", source = "email")
    @Mapping(target = "subject",
            expression = "java(subjectRepository.findById(dto.subjectId()).orElseThrow())")
    @Mapping(target = "avatar",
            expression = "java(resourceRepository.saveAvatarUrl(dto.avatarUrl()))")
    void updateModel(@MappingTarget Teacher model, UpdateTeacherRequestDto dto,
                     @Context EmailRepository repository,
                     @Context SubjectRepository subjectRepository,
                     @Context ResourceRepository resourceRepository);

    default Email mapEmail(String email, @Context EmailRepository repository) {
        return repository.findByValue(email).orElseGet(
                () -> repository.save(new Email().setValue(email))
        );
    }

    @AfterMapping
    default TeacherDto fetchColor(@MappingTarget TeacherDto teacherDto,
                                  @Context TeacherColorMapper colorMapper,
                                  @Context TeacherColorRepository colorRepository) {
        return colorMapper.fetchColor(teacherDto, colorRepository);
    }

    @AfterMapping
    default TeacherDtoWithoutEmail fetchColorAndWithoutEmail(
            @MappingTarget TeacherDtoWithoutEmail teacherDto,
            @Context TeacherColorMapper colorMapper,
            @Context TeacherColorRepository colorRepository
    ) {
        return colorMapper.fetchColorAndWithoutEmail(teacherDto, colorRepository);
    }
}
