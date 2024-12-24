package ru.pereguzochka.telegram_bot.handler.direction_handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.pereguzochka.telegram_bot.bot.TelegramBot;
import ru.pereguzochka.telegram_bot.cache.LessonCache;
import ru.pereguzochka.telegram_bot.client.BackendServiceClient;
import ru.pereguzochka.telegram_bot.dto.LessonDto;
import ru.pereguzochka.telegram_bot.handler.UpdateHandler;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class LessonsHandler implements UpdateHandler {

    private final LessonsAttribute attribute;
    private final TelegramBot bot;
    private final BackendServiceClient backendServiceClient;
    private final LessonCache lessonCache;

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("/lessons");
    }

    @Override
    public void compute(Update update) {
        List<LessonDto> lessons = backendServiceClient.getAllLessons();
        lessons.forEach(lesson -> lessonCache.put(lesson.getId(), lesson));

        bot.edit(attribute.getText(), attribute.createMarkup(), update);
    }
}
