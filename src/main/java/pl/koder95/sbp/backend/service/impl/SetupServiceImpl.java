package pl.koder95.sbp.backend.service.impl;

import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.koder95.sbp.backend.dto.BookingDto;
import pl.koder95.sbp.backend.dto.CreateAdminRequestDto;
import pl.koder95.sbp.backend.dto.CreateAdminResponseDto;
import pl.koder95.sbp.backend.exception.AdminAccountAlreadyExists;
import pl.koder95.sbp.backend.factory.ExamplesFactory;
import pl.koder95.sbp.backend.model.Authority;
import pl.koder95.sbp.backend.model.Email;
import pl.koder95.sbp.backend.model.User;
import pl.koder95.sbp.backend.repository.EmailRepository;
import pl.koder95.sbp.backend.repository.UserRepository;
import pl.koder95.sbp.backend.service.SetupService;

@Service
@RequiredArgsConstructor
public class SetupServiceImpl implements SetupService {
    private final UserRepository userRepository;
    private final EmailRepository emailRepository;
    private final PasswordEncoder passwordEncoder;
    private final ExamplesFactory examplesFactory;

    @Override
    public CreateAdminResponseDto createAdmin(CreateAdminRequestDto requestDto) {
        if (userRepository.existsByAuthority(Authority.ROLE_ADMIN)) {
            throw new AdminAccountAlreadyExists("cannot create admin account when it exists");
        }
        Email email = emailRepository.findByValue(requestDto.email())
                .orElseGet(() -> emailRepository.save(
                        new Email().setValue(requestDto.email())
                ));
        User entity = new User();
        entity.setEmail(email);
        entity.setAuthority(Authority.ROLE_ADMIN);
        entity.setPasswordHash(passwordEncoder.encode(requestDto.password()));
        entity.setZoneId(requestDto.zoneId() == null
                ? ZoneId.systemDefault() : requestDto.zoneId()
        );
        User saved = userRepository.save(entity);
        return new CreateAdminResponseDto(saved.getUuid());
    }

    @Override
    public Page<BookingDto> installExamples(int step, Pageable pageable) {
        return examplesFactory.createExamples(step, pageable);
    }
}
