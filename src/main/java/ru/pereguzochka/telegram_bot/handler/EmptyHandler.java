package ru.pereguzochka.telegram_bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;

@Component
@RequiredArgsConstructor
public class EmptyHandler implements UpdateHandler {
    private final TelegramBot telegramBot;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/empty");
    }

    @Override
    public void compute(Update update) {
        telegramBot.answer(update);
    }
}
