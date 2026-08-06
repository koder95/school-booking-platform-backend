package pl.koder95.sbp.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.koder95.sbp.backend.dto.BookingDto;
import pl.koder95.sbp.backend.dto.CreateAdminRequestDto;
import pl.koder95.sbp.backend.dto.CreateAdminResponseDto;

public interface SetupService {
    CreateAdminResponseDto createAdmin(CreateAdminRequestDto requestDto);

    Page<BookingDto> installExamples(int step, Pageable pageable);
}
