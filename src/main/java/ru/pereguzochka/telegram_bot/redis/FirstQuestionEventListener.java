package ru.pereguzochka.telegram_bot.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.RegistrationEvent;
import ru.pereguzochka.telegram_bot.sender.FirstQuestionEventSender;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class FirstQuestionEventListener implements MessageListener {
    private final ObjectMapper objectMapper;
    private final FirstQuestionEventSender firstQuestionEventSender;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("First question registration event: {}", messageBody);
        try {
            RegistrationEvent registrationEvent = objectMapper.readValue(messageBody, RegistrationEvent.class);
            firstQuestionEventSender.send(registrationEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
