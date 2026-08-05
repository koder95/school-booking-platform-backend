package pl.koder95.sbp.backend.service;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.koder95.sbp.backend.dto.CreateStudentRequestDto;
import pl.koder95.sbp.backend.dto.StudentDto;

public interface StudentService {
    Page<StudentDto> getAll(Pageable pageable);

    StudentDto get(UUID studentUuid);

    StudentDto create(CreateStudentRequestDto dto);

    long count();
}
