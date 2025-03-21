package ru.pereguzochka.telegram_bot.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.RegistrationEvent;
import ru.pereguzochka.telegram_bot.sender.QRSender;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class QRSenderEventListener implements MessageListener {

    private final ObjectMapper objectMapper;
    private final QRSender qrSender;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("QR sender event: {}", messageBody);
        try {
            RegistrationEvent registrationEvent = objectMapper.readValue(messageBody, RegistrationEvent.class);
            qrSender.send(registrationEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
