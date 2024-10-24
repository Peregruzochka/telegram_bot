package ru.pereguzochka.telegram_bot.handler.start_handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

@Component
@RequiredArgsConstructor
public class StartHandler implements UpdateHandler {

    private final TelegramBot bot;
    private final StartAttribute attribute;

    public boolean isApplicable(Update update) {
        return update.getMessage().getText().equals("/start");
    }

    public void compute(Update update) {
        Long chatId = update.getMessage().getChatId();
        //TODO получить данные из back-end
        bot.send(attribute.getText(), attribute.getMarkup(), chatId);
    }
}
