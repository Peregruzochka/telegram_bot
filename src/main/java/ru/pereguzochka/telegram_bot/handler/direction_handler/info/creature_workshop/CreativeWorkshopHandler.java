package ru.pereguzochka.telegram_bot.handler.direction_handler.info.creature_workshop;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

@Component
@RequiredArgsConstructor
public class CreativeWorkshopHandler implements UpdateHandler {
    private final CreativeWorkshopAttribute attribute;
    private final TelegramBot bot;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/creative_workshop");
    }

    @Override
    public void compute(Update update) {
        bot.edit(attribute.getText(), attribute.createMarkup(), update);
    }
}
