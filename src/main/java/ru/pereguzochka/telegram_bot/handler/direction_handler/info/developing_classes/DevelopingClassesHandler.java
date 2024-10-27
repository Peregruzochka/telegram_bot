package ru.pereguzochka.telegram_bot.handler.direction_handler.info.developing_classes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

@Component
@RequiredArgsConstructor
public class DevelopingClassesHandler implements UpdateHandler {

    private final DevelopingClassesAttribute attribute;
    private final TelegramBot bot;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/developing_classes");
    }

    @Override
    public void compute(Update update) {
        bot.edit(attribute.getText(), attribute.createMarkup(), update);
    }
}
