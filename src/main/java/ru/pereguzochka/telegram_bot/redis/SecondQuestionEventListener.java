package ru.pereguzochka.telegram_bot.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.RegistrationEvent;
import ru.pereguzochka.telegram_bot.sender.SecondQuestionEventSender;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
public class SecondQuestionEventListener implements MessageListener {
    private final ObjectMapper objectMapper;
    private final SecondQuestionEventSender secondQuestionEventSender;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("Second question registration event: {}", messageBody);
        try {
            RegistrationEvent registrationEvent = objectMapper.readValue(messageBody, RegistrationEvent.class);
            secondQuestionEventSender.send(registrationEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
