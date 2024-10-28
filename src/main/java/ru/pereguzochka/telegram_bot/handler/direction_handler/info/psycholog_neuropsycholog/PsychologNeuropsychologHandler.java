package ru.pereguzochka.telegram_bot.handler.direction_handler.info.psycholog_neuropsycholog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.handler.direction_handler.info.logoped.LogopedAttribute;

@Component
@RequiredArgsConstructor
public class PsychologNeuropsychologHandler implements UpdateHandler {
    private final PsychologNeuropsychologAttribute attribute;
    private final TelegramBot bot;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/psycholog_neuropsycholog");
    }

    @Override
    public void compute(Update update) {
        bot.edit(attribute.getText(), attribute.createMarkup(), update);
    }
}
