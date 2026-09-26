package pl.koder95.sbp.backend.service;

import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.koder95.sbp.backend.dto.CreateSubjectRequestDto;
import pl.koder95.sbp.backend.dto.SubjectDto;
import pl.koder95.sbp.backend.dto.UpdateSubjectRequestDto;

public interface SubjectService {
    SubjectDto create(CreateSubjectRequestDto requestDto);

    SubjectDto get(Long id);

    SubjectDto getByName(String name);

    SubjectDto update(Long id, UpdateSubjectRequestDto requestDto);

    SubjectDto delete(Long id);

    Page<SubjectDto> getAll(Pageable pageable);

    List<SubjectDto> getAll(Set<Long> ids);
}
