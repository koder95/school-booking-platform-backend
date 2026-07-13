package pl.koder95.sbp.backend.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.koder95.sbp.backend.config.MapperConfig;
import pl.koder95.sbp.backend.dto.CreateStudentRequestDto;
import pl.koder95.sbp.backend.dto.StudentDto;
import pl.koder95.sbp.backend.model.Student;
import pl.koder95.sbp.backend.repository.EmailRepository;

@Mapper(config = MapperConfig.class)
public interface StudentMapper {
    @Mapping(target = "email", source = "email.value")
    StudentDto toDto(Student student, @Context EmailRepository emailRepository);

    @Mapping(target = "email", expression = "java(emailRepository"
            + ".findByValue(dto.email()).orElseThrow())")
    Student toModel(CreateStudentRequestDto dto, EmailRepository emailRepository);

}
