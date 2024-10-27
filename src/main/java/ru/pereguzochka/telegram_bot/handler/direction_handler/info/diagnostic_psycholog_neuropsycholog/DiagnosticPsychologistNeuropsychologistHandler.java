package ru.pereguzochka.telegram_bot.handler.direction_handler.info.diagnostic_psycholog_neuropsycholog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

@Component
@RequiredArgsConstructor
public class DiagnosticPsychologistNeuropsychologistHandler implements UpdateHandler {
    private final DiagnosticPsychologistNeuropsychologistAttribute attribute;
    private final TelegramBot bot;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/diagnostic_psycholog_neuropsycholog");
    }

    @Override
    public void compute(Update update) {
        bot.edit(attribute.getText(), attribute.createMarkup(), update);
    }
}
