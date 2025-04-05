package ru.pereguzochka.telegram_bot.handler.lesson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedLessonByTelegramId;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SelectLessonHandler implements UpdateHandler {
    private final TelegramBot bot;
    private final LessonDescriptionAttribute lessonDescriptionAttribute;
    private final BotBackendClient botBackendClient;
    private final SelectedLessonByTelegramId selectedLessonByTelegramId;

    @Override
    public boolean isApplicable(Update update) {
        return callbackStartWith(update, "/select-lesson:");
    }

    @Override
    public void compute(Update update) {
        UUID lessonId = UUID.fromString(getCallbackPayload(update, "/select-lesson:"));
        String telegramId = bot.extractTelegramId(update).toString();

        LessonDto lesson = botBackendClient.getLesson(lessonId);

        selectedLessonByTelegramId.put(telegramId, lesson);

        bot.edit(
                lessonDescriptionAttribute.generateText(lesson),
                lessonDescriptionAttribute.createMarkup(),
                update
        );

        log.info("telegramId: {} -> /select-lesson: {}", telegramId, lesson.getId());
    }
}
