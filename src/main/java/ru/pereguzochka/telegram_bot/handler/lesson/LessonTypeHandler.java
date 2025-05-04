package ru.pereguzochka.telegram_bot.handler.lesson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class LessonTypeHandler implements UpdateHandler {

    private final LessonTypeAttribute lessonTypeAttribute;
    private final TelegramBot telegramBot;

    @Override
    public boolean isApplicable(Update update) {
        return hasCallback(update, "/lesson-type");
    }

    @Override
    public void compute(Update update) {
        String text = lessonTypeAttribute.getText();
        InlineKeyboardMarkup markup = lessonTypeAttribute.createMarkup();

        telegramBot.edit(text, markup,  update);

        Long telegramId = telegramBot.extractTelegramId(update);
        log.info("telegramId: {} -> /lesson-type", telegramId);
    }
}
