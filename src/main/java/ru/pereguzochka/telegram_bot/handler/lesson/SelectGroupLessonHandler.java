package ru.pereguzochka.telegram_bot.handler.lesson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.client.BotBackendClient;
import ru.pereguzochka.telegram_bot.dto.GroupLessonDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;
import ru.pereguzochka.telegram_bot.redis.redis_repository.dto_cache.SelectedGroupLessonByTelegramId;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SelectGroupLessonHandler implements UpdateHandler {

    private final TelegramBot telegramBot;
    private final BotBackendClient backendClient;
    private final SelectedGroupLessonByTelegramId selectedGroupLessonByTelegramId;
    private final GroupLessonDescriptionAttribute groupLessonDescriptionAttribute;

    @Override
    public boolean isApplicable(Update update) {
        return callbackStartWith(update, "/select-group-lesson");
    }

    @Override
    public void compute(Update update) {
        UUID groupLessonId = UUID.fromString(getCallbackPayload(update, "/select-group-lesson:"));
        String telegramId = telegramBot.extractTelegramId(update).toString();

        GroupLessonDto groupLesson = backendClient.getGroupLesson(groupLessonId);

        selectedGroupLessonByTelegramId.put(telegramId, groupLesson);

        telegramBot.edit(
                groupLessonDescriptionAttribute.generateText(groupLesson),
                groupLessonDescriptionAttribute.createMarkup(),
                update
        );

        log.info("telegramId: {} -> /select-group-lesson: {}", telegramId, groupLesson.getId());
    }
}
