package ru.pereguzochka.telegram_bot.redis.redis_broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.dto.GroupRegistrationEvent;
import ru.pereguzochka.telegram_bot.sender.NotConfirmedEventSender;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotConfirmedGroupEventListener implements MessageListener {
    private final ObjectMapper objectMapper;
    private final NotConfirmedEventSender notConfirmedEventSender;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("Not confirmed group registration event: {}", messageBody);
        try {
            GroupRegistrationEvent registrationEvent = objectMapper.readValue(messageBody, GroupRegistrationEvent.class);
            notConfirmedEventSender.send(registrationEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
