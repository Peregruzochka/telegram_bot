package ru.pereguzochka.telegram_bot.sender;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;

@Component
@RequiredArgsConstructor
public class RestartBotMessageSender {
    private final TelegramBot telegramBot;
    private final RestartBotMessageAttribute restartBotMessageAttribute;

    public void send(Update update) {
        if (update.hasCallbackQuery()) {
            telegramBot.answer(update);
        }
        telegramBot.send(restartBotMessageAttribute.getText(), update);
    }
}
