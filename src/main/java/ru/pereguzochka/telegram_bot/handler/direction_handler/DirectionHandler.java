package ru.pereguzochka.telegram_bot.handler.direction_handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

@Component
@Slf4j
@RequiredArgsConstructor
public class DirectionHandler implements UpdateHandler {
    private final DirectionAttribute attribute;
    private final TelegramBot bot;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/schedule");
    }

    @Override
    public void compute(Update update) {
        bot.edit(attribute.getText(), attribute.getMarkup(), update);
    }
}
