package pl.koder95.sbp.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.koder95.sbp.backend.dto.EmailDeliveryInfoDto;
import pl.koder95.sbp.backend.dto.SendEmailRequestDto;

public interface EmailDeliveryService {
    void requestAsyncSend(SendEmailRequestDto dto);

    Page<EmailDeliveryInfoDto> getAll(Pageable pageable);

    void sendAll();
}
