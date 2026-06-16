package pl.koder95.sbp.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.koder95.sbp.backend.dto.EmailDto;
import pl.koder95.sbp.backend.dto.EmailValueDto;
import pl.koder95.sbp.backend.exception.EmailAlreadyExistsException;
import pl.koder95.sbp.backend.exception.InvalidEmailValueException;

public interface EmailService {
    EmailDto register(EmailValueDto dto)
            throws EmailAlreadyExistsException, InvalidEmailValueException;

    EmailDto get(Long id);

    Page<EmailDto> getAll(Pageable pageable);

    void deleteById(Long id);

    EmailDto findByValue(String value);
}
