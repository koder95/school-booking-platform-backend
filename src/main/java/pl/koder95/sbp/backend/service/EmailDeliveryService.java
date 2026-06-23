package pl.koder95.sbp.backend.service;

import pl.koder95.sbp.backend.dto.SendEmailRequestDto;

public interface EmailDeliveryService {
    void send(SendEmailRequestDto dto);
}
