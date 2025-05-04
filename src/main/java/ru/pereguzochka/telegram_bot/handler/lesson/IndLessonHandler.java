package ru.pereguzochka.telegram_bot.handler.lesson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class IndLessonHandler implements UpdateHandler {
    private final BotBackendClient backendClient;
    private final IndLessonAttribute indLessonAttribute;
    private final TelegramBot telegramBot;

    @Override
    public boolean isApplicable(Update update) {
        return hasCallback(update, "/ind-lesson");
    }

    @Override
    public void compute(Update update) {
        List<LessonDto> lessons = backendClient.getAllLessons();

        String text = indLessonAttribute.getText();
        InlineKeyboardMarkup markup = indLessonAttribute.generateIndLessonMarkup(lessons);

        telegramBot.edit(text, markup, update);

        Long telegramId = telegramBot.extractTelegramId(update);
        log.info("telegramId: {} -> /ind-lesson", telegramId);
    }
}
