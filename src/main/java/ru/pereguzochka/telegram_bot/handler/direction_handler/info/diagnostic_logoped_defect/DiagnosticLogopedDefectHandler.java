package ru.pereguzochka.telegram_bot.handler.direction_handler.info.diagnostic_logoped_defect;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

@Component
@RequiredArgsConstructor
public class DiagnosticLogopedDefectHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final DiagnosticLogopedDefectAttribute attribute;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/diagnostic_logoped_defect");
    }

    @Override
    public void compute(Update update) {
        bot.edit(attribute.getText(), attribute.createMarkup(), update);
    }
}
