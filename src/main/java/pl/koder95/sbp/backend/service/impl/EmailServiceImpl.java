package pl.koder95.sbp.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.koder95.sbp.backend.dto.EmailDto;
import pl.koder95.sbp.backend.dto.EmailValueDto;
import pl.koder95.sbp.backend.exception.EmailAlreadyExistsException;
import pl.koder95.sbp.backend.exception.EntityNotFoundException;
import pl.koder95.sbp.backend.exception.InvalidEmailValueException;
import pl.koder95.sbp.backend.mapper.EmailMapper;
import pl.koder95.sbp.backend.repository.EmailRepository;
import pl.koder95.sbp.backend.service.EmailService;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final EmailRepository emailRepository;
    private final EmailMapper emailMapper;

    @Override
    public EmailDto register(EmailValueDto dto)
            throws EmailAlreadyExistsException, InvalidEmailValueException {
        if (emailRepository.existsByValue(dto.value())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        if (dto.value() == null || !dto.value().contains("@")) {
            throw new InvalidEmailValueException("Invalid email value");
        }
        return emailMapper.toResponseDto(emailRepository.save(emailMapper.toModel(dto)));
    }

    @Override
    public EmailDto get(Long id) {
        return emailRepository.findById(id)
                .map(emailMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Email not found"));
    }

    @Override
    public Page<EmailDto> getAll(Pageable pageable) {
        return emailRepository.findAll(pageable).map(emailMapper::toResponseDto);
    }

    @Override
    public void deleteById(Long id) {
        emailRepository.deleteById(id);
    }

    @Override
    public EmailDto findByValue(String value) {
        return emailRepository.findByValue(value)
                .map(emailMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Email not found"));
    }
}
