package ru.pereguzochka.telegram_bot.sender;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.dto.GroupRegistrationEvent;
import ru.pereguzochka.telegram_bot.dto.RegistrationEvent;

@Component
@RequiredArgsConstructor
public class SecondQuestionEventSender {
    private final TelegramBot bot;
    private final SecondQuestionEventAttribute attribute;

    public void send(RegistrationEvent event) {
        bot.send(attribute.generateText(event), attribute.createMarkup(), event.getTelegramId());
    }

    public void send(GroupRegistrationEvent event) {
        bot.send(attribute.generateText(event), attribute.createMarkup(), event.getTelegramId());
    }
}
