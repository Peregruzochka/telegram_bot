package ru.pereguzochka.telegram_bot.sender;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.dto.GroupRegistrationEvent;
import ru.pereguzochka.telegram_bot.dto.RegistrationEvent;


@Component
@RequiredArgsConstructor
public class FirstQuestionEventSender {
    private final TelegramBot bot;
    private final FirstQuestionEventAttribute firstQuestionEventAttribute;

    public void send(RegistrationEvent event) {
        bot.send(
                firstQuestionEventAttribute.generateText(event),
                firstQuestionEventAttribute.generateMarkup(event),
                event.getTelegramId()
        );
    }

    public void send(GroupRegistrationEvent event) {
        bot.send(
                firstQuestionEventAttribute.generateText(event),
                firstQuestionEventAttribute.generateMarkup(event),
                event.getTelegramId()
        );
    }
}
