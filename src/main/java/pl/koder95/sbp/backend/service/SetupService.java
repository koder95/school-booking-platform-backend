package pl.koder95.sbp.backend.service;

import pl.koder95.sbp.backend.dto.CreateAdminRequestDto;
import pl.koder95.sbp.backend.dto.CreateAdminResponseDto;

public interface SetupService {
    CreateAdminResponseDto createAdmin(CreateAdminRequestDto requestDto);
}
