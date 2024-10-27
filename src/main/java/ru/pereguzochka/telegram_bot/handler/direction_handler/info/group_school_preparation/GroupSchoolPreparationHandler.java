package ru.pereguzochka.telegram_bot.handler.direction_handler.info.group_school_preparation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

@Component
@RequiredArgsConstructor
public class GroupSchoolPreparationHandler implements UpdateHandler {

    private final GroupSchoolPreparationAttribute attribute;
    private final TelegramBot bot;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/group_school_preparation");
    }

    @Override
    public void compute(Update update) {
        bot.edit(attribute.getText(), attribute.createMarkup(), update);
    }
}
