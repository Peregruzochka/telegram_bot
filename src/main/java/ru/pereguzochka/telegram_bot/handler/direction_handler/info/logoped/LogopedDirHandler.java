package ru.pereguzochka.telegram_bot.handler.direction_handler.info.logoped;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

@Component
@RequiredArgsConstructor
public class LogopedDirHandler implements UpdateHandler {
    private final LogopedAttribute attribute;
    private final TelegramBot telegramBot;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/logoped");
    }

    @Override
    public void compute(Update update) {
        telegramBot.edit(attribute.getText(), attribute.createMarkup(), update);
    }
}

