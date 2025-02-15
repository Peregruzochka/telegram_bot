package ru.pereguzochka.telegram_bot.sender;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.dto.RegistrationEvent;

@Component
@RequiredArgsConstructor
public class NotConfirmedEventSender {

    private final TelegramBot bot;
    private final NotConfirmedEventAttribute notConfirmedEventAttribute;

    public void send(RegistrationEvent event) {
        bot.send(
                notConfirmedEventAttribute.generateText(event),
                notConfirmedEventAttribute.generateMarkup(event),
                event.getTelegramId()
        );
    }
}
